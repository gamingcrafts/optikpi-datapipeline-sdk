<?php

namespace Optikpi\DataPipeline\Tests;

use Optikpi\DataPipeline\Models\AccountEvent;
use Optikpi\DataPipeline\Models\DepositEvent;
use Optikpi\DataPipeline\Models\GamingActivityEvent;
use Optikpi\DataPipeline\Models\ReferFriendEvent;
use Optikpi\DataPipeline\Models\WalletBalanceEvent;
use Optikpi\DataPipeline\Models\WithdrawEvent;
use PHPUnit\Framework\TestCase;

class EventCategoryTest extends TestCase
{
    private const BASE = [
        'account_id' => 'acc_example_001',
        'workspace_id' => 'ws_example_001',
        'user_id' => '1',
        'event_id' => 'e1',
        'event_name' => 'Something',
        'event_time' => '2026-01-15T10:30:00Z',
    ];

    public function models(): array
    {
        return [
            'Account' => [AccountEvent::class, 'Account'],
            'Deposit' => [DepositEvent::class, 'Deposit'],
            'Withdraw' => [WithdrawEvent::class, 'Withdraw'],
            'Gaming Activity' => [GamingActivityEvent::class, 'Gaming Activity'],
            'Wallet Balance' => [WalletBalanceEvent::class, 'Wallet Balance'],
            'Refer Friend' => [ReferFriendEvent::class, 'Refer Friend'],
        ];
    }

    /** @dataProvider models */
    public function testMissingCategoryIsRejectedAndNotFilledIn(string $class, string $category): void
    {
        $event = new $class(self::BASE);
        $this->assertNull($event->event_category);
        $this->assertContains('event_category is required', $event->validate()['errors']);
    }

    /** @dataProvider models */
    public function testOwnCategoryIsAccepted(string $class, string $category): void
    {
        $event = new $class(self::BASE + ['event_category' => $category]);
        $this->assertNotContains('event_category is required', $event->validate()['errors']);
    }

    /** @dataProvider models */
    public function testWrongCategoryIsRejected(string $class, string $category): void
    {
        $event = new $class(self::BASE + ['event_category' => 'Other']);
        $this->assertFalse($event->validate()['isValid']);
    }
}
