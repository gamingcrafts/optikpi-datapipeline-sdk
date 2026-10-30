<?php

namespace Optikpi\DataPipeline\Tests;

use Optikpi\DataPipeline\Models\AccountEvent;
use Optikpi\DataPipeline\Models\BigQueryValidation;
use Optikpi\DataPipeline\Models\CustomerExtEvent;
use Optikpi\DataPipeline\Models\CustomerProfile;
use Optikpi\DataPipeline\Models\DepositEvent;
use Optikpi\DataPipeline\Models\GamingActivityEvent;
use Optikpi\DataPipeline\Models\ReferFriendEvent;
use Optikpi\DataPipeline\Models\WalletBalanceEvent;
use Optikpi\DataPipeline\Models\WithdrawEvent;
use PHPUnit\Framework\TestCase;

class StorageValidationTest extends TestCase
{
    private static function nowIso(): string
    {
        return (new \DateTime('now', new \DateTimeZone('UTC')))->format('Y-m-d\TH:i:s.v\Z');
    }

    private static function event(): array
    {
        return [
            'account_id' => 'acc',
            'workspace_id' => 'ws',
            'user_id' => '1',
            'event_id' => 'e1',
            'event_name' => 'Something',
            'event_time' => self::nowIso(),
        ];
    }

    // ---- checkType -------------------------------------------------------------------------------------------

    public static function typeCases(): array
    {
        // [type, value, accepted]: what BigQuery was measured to store (dev probe, 2026-10-29), except where
        // marked as a deliberate rejection of a value it stores as junk, or a deliberate acceptance of a value
        // the API serialises before storage.
        $cases = [
            ['STRING', 'abc', true],
            ['STRING', '', true],
            ['STRING', 15466, false],
            ['STRING', 1.5, false],
            ['STRING', true, false],
            ['STRING', new \stdClass(), false],
            ['STRING', ['a' => 1], false],
            ['STRING', [1, 2], false],

            ['NUMERIC', 10, true],
            ['NUMERIC', 10.5, true],
            ['NUMERIC', -3, true],
            ['NUMERIC', '10.5', true],
            ['NUMERIC', '-10', true],
            ['NUMERIC', '1e5', true],
            ['NUMERIC', ' 10', true],
            ['NUMERIC', '10 ', true],
            ['NUMERIC', '+10', true],
            ['NUMERIC', '.5', true],
            ['NUMERIC', '10.', true],
            ['NUMERIC', "\t10\n", true],
            ['NUMERIC', "\u{00a0}10", false], // padding is ASCII whitespace only
            ['NUMERIC', '', false],
            ['NUMERIC', true, false],
            ['NUMERIC', '1,000', false],
            ['NUMERIC', 'abc', false], // deliberate: BigQuery stores garbage
            ['NUMERIC', INF, false],
            ['NUMERIC', NAN, false],

            ['INTEGER', 3, true],
            ['INTEGER', 3.0, true],
            ['INTEGER', '3', true],
            ['INTEGER', '+3', true],
            ['INTEGER', 3.5, false],
            ['INTEGER', '3.5', false],
            ['INTEGER', '', false],
            ['INTEGER', true, false],
            ['INTEGER', ' 3', false],
            ['INTEGER', '3e2', false],

            ['BOOLEAN', true, true],
            ['BOOLEAN', false, true],
            ['BOOLEAN', 'true', true],
            ['BOOLEAN', 'TRUE', true],
            ['BOOLEAN', 'True', true],
            ['BOOLEAN', 'false', true],
            ['BOOLEAN', 1, true],
            ['BOOLEAN', 0, true],
            ['BOOLEAN', '1', true],
            ['BOOLEAN', '0', true],
            ['BOOLEAN', 'yes', false], // deliberate: BigQuery stores it as true
            ['BOOLEAN', 2, false],
            ['BOOLEAN', '', false],

            ['TIMESTAMP', '2026-10-29T12:32:13.562Z', true],
            ['TIMESTAMP', '2026-10-29 12:32:13', true],
            ['TIMESTAMP', '2026-10-29 12:32:13 UTC', true],
            ['TIMESTAMP', '2026-10-29T12:32:13 UTC', true],
            ['TIMESTAMP', '2026-10-29T12:32:13+05:30', true],
            ['TIMESTAMP', '2026-10-29T12:32:13-03:00', true],
            ['TIMESTAMP', '2026-10-29 12:30', true],
            ['TIMESTAMP', '2026-10-29T12:32:13.123456789Z', true],
            ['TIMESTAMP', 1761741133562000, true],
            ['TIMESTAMP', 1761741133, true],
            ['TIMESTAMP', 1761741133.5, true],
            ['TIMESTAMP', '1761741133', true],
            ['TIMESTAMP', '0001-01-01T00:00:00Z', true],
            ['TIMESTAMP', '0099-12-31 23:59:59', true],
            ['TIMESTAMP', '2026-10-29T12:32:13-18:00', true],
            ['TIMESTAMP', '2026-10-29T12:32:13+18:59', true],
            ['TIMESTAMP', '0000-01-01T00:00:00Z', false],
            ['TIMESTAMP', '2026-10-29T12:32:13+05:99', false],
            ['TIMESTAMP', '2026-10-29T12:32:13+19:00', false],
            ['TIMESTAMP', '2026-10-29', false],
            ['TIMESTAMP', '2026-10-29T12:32:13 Asia/Kolkata', false],
            ['TIMESTAMP', '2026-10-29T12:32:13+0530', false],
            ['TIMESTAMP', '2026-10-29T12:32:13+05', false],
            ['TIMESTAMP', '2026-1-5 1:2:3', false],
            ['TIMESTAMP', '20261029T123213Z', false],
            ['TIMESTAMP', 'Oct 29 2026', false],
            ['TIMESTAMP', '', false],
            ['TIMESTAMP', '   ', false],
            ['TIMESTAMP', '2026-02-30 00:00:00', false],
            ['TIMESTAMP', '2026-10-29 24:00:00', false],
            ['TIMESTAMP', '2026-10-29 12:60:00', false],
            ['TIMESTAMP', '2026-10-29 12:00:60', false],
            ['TIMESTAMP', '2026-10-29 12:32:13 UTC+5', false],
            ['TIMESTAMP', '2026-10-29T12:32:13z', false],
            ['TIMESTAMP', '2026-10-29t12:32:13Z', false],
            ['TIMESTAMP', "2026-10-29T12:32:13Z\n", false],
            ['TIMESTAMP', true, false],
            ['TIMESTAMP', 'now', false],
            ['TIMESTAMP', 'tomorrow', false],
            ['TIMESTAMP', '+1 day', false],
            ['TIMESTAMP', 'not a date', false],
            ['TIMESTAMP', INF, false],
            ['TIMESTAMP', new \stdClass(), false],

            ['DATE', '1990-05-17', true],
            ['DATE', '1990-5-7', true],
            ['DATE', 7000, true],
            ['DATE', '7000', true],
            ['DATE', '0001-01-01', true],
            ['DATE', '0000-01-01', false],
            ['DATE', '1990-02-30', false],
            ['DATE', '17/05/1990', false],
            ['DATE', '', false],
            ['DATE', '2026-10-29T12:32:13Z', false],
            ['DATE', '2026-10-29 12:32:13', false],
            ['DATE', true, false],

            ['JSON', '{"a":1}', true],
            ['JSON', '[1,2]', true],
            ['JSON', '"str"', true],
            ['JSON', 'null', true],
            ['JSON', ['a' => 1], true], // deliberate: the API serialises arrays before storage
            ['JSON', [1, 2], true],     // deliberate: the API serialises arrays before storage
            ['JSON', (object) ['a' => 1], true],
            ['JSON', 'bad{', false],
            ['JSON', '', false],
            ['JSON', 5, false],
            ['JSON', true, false],
        ];

        $named = [];
        foreach ($cases as $i => $case) {
            $named[$i . ' ' . $case[0] . ' ' . var_export($case[1], true)] = $case;
        }
        return $named;
    }

    /**
     * @dataProvider typeCases
     */
    public function testCheckType(string $type, $value, bool $accepted): void
    {
        $this->assertSame($accepted, BigQueryValidation::checkType('f', $value, $type) === null);
    }

    // ---- validateStorage -------------------------------------------------------------------------------------

    private const RULES = [
        'required' => ['a', 'b'],
        'types' => ['a' => 'STRING', 'n' => 'NUMERIC', 't' => 'TIMESTAMP'],
        'partition' => 't',
    ];

    private static function now(): float
    {
        return (float) (new \DateTime('2026-10-29T00:00:00Z'))->format('U') * 1000;
    }

    public static function missingRequiredCases(): array
    {
        return [
            'missing' => [[]],
            'null' => [['a' => null, 'b' => null]],
            'empty string' => [['a' => '', 'b' => '']],
        ];
    }

    /**
     * @dataProvider missingRequiredCases
     */
    public function testRequiredFieldsAreRejected(array $record): void
    {
        $errors = BigQueryValidation::validateStorage($record, self::RULES, self::now());
        $this->assertContains('a is required', $errors);
        $this->assertContains('b is required', $errors);
    }

    public function testNullAndAbsentOptionalFieldsAreAccepted(): void
    {
        $this->assertSame([], BigQueryValidation::validateStorage(['a' => 'x', 'b' => 'y', 'n' => null], self::RULES, self::now()));
    }

    public static function partitionCases(): array
    {
        return [
            ['2016-10-15T00:00:00Z', false],
            ['2016-11-15T00:00:00Z', true],
            ['2027-10-01T00:00:00Z', true],
            ['2027-12-01T00:00:00Z', false],
            // The window edge is 2016-10-28T12:00Z; the offset moves this one inside it.
            ['2016-10-28T08:00:00-05:00', true],
            ['2016-10-28T08:00:00Z', false],
        ];
    }

    /**
     * @dataProvider partitionCases
     */
    public function testPartitionWindow(string $t, bool $accepted): void
    {
        $errors = BigQueryValidation::validateStorage(['a' => 'x', 'b' => 'y', 't' => $t], self::RULES, self::now());
        $this->assertSame($accepted, count($errors) === 0);
    }

    public function testNumericTimestampsAreNotPartitionChecked(): void
    {
        foreach ([0, 1761741133, '0', '1761741133'] as $t) {
            $this->assertSame([], BigQueryValidation::validateStorage(['a' => 'x', 'b' => 'y', 't' => $t], self::RULES, self::now()));
        }
    }

    public function testStorageRecordUsesCurrentDeclaredValuesAndOnlyUndeclaredRawKeys(): void
    {
        $event = new AccountEvent(['event_category' => 'Account', 'user_id' => '1', 'extra' => 5]);
        $event->event_category = null;
        $event->user_id = '2';
        $record = BigQueryValidation::storageRecord($event, ['event_category' => 'Account', 'user_id' => '1', 'extra' => 5]);
        $this->assertArrayHasKey('event_category', $record);
        $this->assertNull($record['event_category']);
        $this->assertSame('2', $record['user_id']);
        $this->assertSame(5, $record['extra']);
        $this->assertArrayNotHasKey('rawInput', $record);
    }

    // ---- models ----------------------------------------------------------------------------------------------

    public static function models(): array
    {
        return [
            'AccountEvent' => [AccountEvent::class, 'Account', []],
            'DepositEvent' => [DepositEvent::class, 'Deposit', ['amount' => 10, 'payment_method' => 'credit_card', 'transaction_id' => 't1']],
            'WithdrawEvent' => [WithdrawEvent::class, 'Withdraw', ['amount' => 10, 'payment_method' => 'credit_card', 'transaction_id' => 't1']],
            'GamingActivityEvent' => [GamingActivityEvent::class, 'Gaming Activity', ['game_id' => '15466', 'game_title' => 'Aviator']],
            'WalletBalanceEvent' => [WalletBalanceEvent::class, 'Wallet Balance', ['currency' => 'INR', 'wallet_type' => 'main']],
            'ReferFriendEvent' => [ReferFriendEvent::class, 'Refer Friend', []],
        ];
    }

    /**
     * @dataProvider models
     */
    public function testRejectsMissingEventCategoryAndDoesNotFillOneIn(string $model, string $category, array $extra): void
    {
        $instance = new $model(array_merge(self::event(), $extra));
        $result = $instance->validate();
        $this->assertFalse($result['isValid']);
        $this->assertContains('event_category is required', $result['errors']);
        $this->assertArrayNotHasKey('event_category', $instance->toArray());
    }

    /**
     * @dataProvider models
     */
    public function testAcceptsACompleteRecord(string $model, string $category, array $extra): void
    {
        $result = (new $model(array_merge(self::event(), ['event_category' => $category], $extra)))->validate();
        $this->assertSame(['isValid' => true, 'errors' => []], $result);
    }

    /**
     * @dataProvider models
     */
    public function testWrongEventCategoryIsStillRejected(string $model, string $category, array $extra): void
    {
        $result = (new $model(array_merge(self::event(), ['event_category' => 'Other'], $extra)))->validate();
        $this->assertFalse($result['isValid']);
        $this->assertNotContains('event_category is required', $result['errors']);
    }

    public function testFieldTheModelDoesNotDeclareIsStillTypeChecked(): void
    {
        // A declared column with the wrong type.
        $result = (new GamingActivityEvent(array_merge(self::event(), [
            'event_category' => 'Gaming Activity',
            'game_id' => '1',
            'game_title' => 'Aviator',
            'game_category' => 7,
        ])))->validate();
        $this->assertContains('game_category must be a string', $result['errors']);
    }

    /**
     * @dataProvider models
     */
    public function testEventCategoryUnsetAfterConstructionIsRejected(string $model, string $category, array $extra): void
    {
        $instance = new $model(array_merge(self::event(), ['event_category' => $category], $extra));
        unset($instance->event_category);
        $result = $instance->validate();
        $this->assertFalse($result['isValid']);
        $this->assertContains('event_category is required', $result['errors']);
        $this->assertArrayNotHasKey('event_category', $instance->toArray());
    }

    public function testTwoDigitYearsAreNotShiftedIntoTheEpoch(): void
    {
        // A year-0099 date-time is far outside the partition window, not mapped to 1999 or 2099.
        $errors = BigQueryValidation::validateStorage(['a' => 'x', 'b' => 'y', 't' => '0099-12-31 23:59:59'], self::RULES, self::now());
        $this->assertSame(['t must be within the last 10 years and no more than 1 year in the future'], $errors);
    }

    public function testUndeclaredColumnIsTypeChecked(): void
    {
        // payment_provider_id is a stored column the PHP WithdrawEvent does not declare, so only the raw input
        // carries it: this fails if validate() stops merging the raw input.
        $result = (new WithdrawEvent(array_merge(self::event(), [
            'event_category' => 'Withdraw',
            'amount' => 10,
            'payment_method' => 'credit_card',
            'transaction_id' => 't1',
            'payment_provider_id' => 123,
        ])))->validate();
        $this->assertSame(['payment_provider_id must be a string'], $result['errors']);
    }

    /**
     * @dataProvider models
     */
    public function testEventCategoryClearedAfterConstructionIsRejected(string $model, string $category, array $extra): void
    {
        $instance = new $model(array_merge(self::event(), ['event_category' => $category], $extra));
        $instance->event_category = null;
        $result = $instance->validate();
        $this->assertFalse($result['isValid']);
        $this->assertContains('event_category is required', $result['errors']);
    }

    public function testNumericGameIdIsRejected(): void
    {
        $result = (new GamingActivityEvent(array_merge(self::event(), [
            'event_category' => 'Gaming Activity',
            'game_id' => 15466,
            'game_title' => 'Aviator',
        ])))->validate();
        $this->assertContains('game_id must be a string', $result['errors']);
    }

    public function testWalletPayloadIsRejectedForItsMissingEventCategory(): void
    {
        $result = (new WalletBalanceEvent([
            'workspace_id' => 'ws_example_001',
            'blocked_amount' => 100.0,
            'account_id' => 'acc_example_001',
            'current_cash_balance' => 3065.5,
            'event_id' => 'da49a497-a41f-4e37-b7b2-e10ad0c0ed4d',
            'currency' => 'INR',
            'wallet_type' => 'main',
            'user_id' => '1',
            'current_bonus_balance' => 20.0,
            'event_time' => self::nowIso(),
            'event_name' => 'Balance Update',
            'current_total_balance' => 3085.5,
        ]))->validate();
        $this->assertSame(['event_category is required'], $result['errors']);
    }

    public function testCustomerWithEmptyStringTimestampIsRejected(): void
    {
        $result = (new CustomerProfile([
            'account_id' => 'acc',
            'workspace_id' => 'ws',
            'user_id' => '1',
            'username' => 'u',
            'email' => 'u@example.com',
            'creation_timestamp' => self::nowIso(),
            'self_exclusion_created_time' => '',
        ]))->validate();
        $this->assertContains('self_exclusion_created_time must be a valid ISO 8601 date-time', $result['errors']);
    }

    public function testCustomerCreatedOutsideThePartitionWindowIsRejected(): void
    {
        $result = (new CustomerProfile([
            'account_id' => 'acc',
            'workspace_id' => 'ws',
            'user_id' => '1',
            'username' => 'u',
            'email' => 'u@example.com',
            'creation_timestamp' => '2012-01-01T00:00:00Z',
        ]))->validate();
        $this->assertContains(
            'creation_timestamp must be within the last 10 years and no more than 1 year in the future',
            $result['errors']
        );
    }

    public function testCustomerExtDataMayBeAnObjectOrAJsonString(): void
    {
        $base = ['account_id' => 'acc', 'workspace_id' => 'ws', 'user_id' => '1', 'list_name' => 'l'];
        $this->assertTrue((new CustomerExtEvent($base + ['ext_data' => ['a' => 1]]))->validate()['isValid']);
        $this->assertTrue((new CustomerExtEvent($base + ['ext_data' => '{"a":1}']))->validate()['isValid']);
    }

    public function testRawInputIsNotSerialised(): void
    {
        $event = new AccountEvent(array_merge(self::event(), ['event_category' => 'Account', 'extra' => 'x']));
        $this->assertArrayNotHasKey('rawInput', $event->toArray());
        $this->assertArrayNotHasKey('extra', $event->toArray());
    }
}
