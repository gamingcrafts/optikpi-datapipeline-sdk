<?php

namespace Optikpi\DataPipeline\Tests;

use Optikpi\DataPipeline\Models\PurchaseFunnelEvent;
use PHPUnit\Framework\TestCase;

class PurchaseFunnelEventTest extends TestCase
{
    // 2026-11-04T10:00:00Z
    private const NOW_MS = 1793786400000.0;

    /** Shared with the API's tests: the client and the server must agree on every shape. */
    private static function shapes(): array
    {
        $json = file_get_contents(__DIR__ . '/../../testdata/purchase-funnel-shapes.json');
        return json_decode($json, true, 512, JSON_THROW_ON_ERROR);
    }

    public function shapeCases(): array
    {
        $shapes = self::shapes();
        $cases = [];
        foreach ($shapes['cases'] as $case) {
            $record = ($case['from'] ?? 'base') === 'full' ? $shapes['full'] : $shapes['base'];
            foreach ($case['unset'] ?? [] as $field) {
                unset($record[$field]);
            }
            foreach ($case['set'] ?? [] as $field => $value) {
                $record[$field] = $value;
            }
            $cases[$case['name']] = [$record, $case['valid'], $case['errorField'] ?? null];
        }
        return $cases;
    }

    /** @dataProvider shapeCases */
    public function testValidateMatchesSharedShapes(array $record, bool $valid, ?string $errorField): void
    {
        $result = (new PurchaseFunnelEvent($record))->validate(self::NOW_MS);
        $this->assertSame($valid, $result['isValid'], implode('; ', $result['errors']));
        if (!$valid) {
            $named = array_filter($result['errors'], function ($message) use ($errorField) {
                return strpos($message, $errorField) !== false;
            });
            $this->assertNotEmpty($named, implode('; ', $result['errors']));
        }
    }

    public function testWindowDefaultsToNow(): void
    {
        $base = self::shapes()['base'];
        $recent = gmdate('Y-m-d\TH:i:s\Z', time() - 60);
        $this->assertTrue((new PurchaseFunnelEvent(['event_time' => $recent] + $base))->validate()['isValid']);
        $old = gmdate('Y-m-d\TH:i:s\Z', time() - 11 * 365 * 86400);
        $this->assertFalse((new PurchaseFunnelEvent(['event_time' => $old] + $base))->validate()['isValid']);
    }

    public function testEventCategoryIsNotFilledIn(): void
    {
        $record = self::shapes()['base'];
        unset($record['event_category']);
        $this->assertNull((new PurchaseFunnelEvent($record))->event_category);
    }

    public function testConstantsInContractOrder(): void
    {
        $this->assertSame([
            'start', 'browse', 'configure', 'cart', 'checkout', 'payment', 'payment_failed', 'complete',
            'failed', 'cancelled', 'exit', 'stalled', 'abandoned',
        ], PurchaseFunnelEvent::FUNNEL_STAGES);
        $this->assertSame([
            'payment_declined', 'insufficient_funds', 'provider_timeout', 'limit_reached', 'kyc_required',
            'geo_blocked', 'draw_closed', 'technical_error', 'user_cancelled',
        ], PurchaseFunnelEvent::FAILURE_REASONS);
    }
}
