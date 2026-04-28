<?php

namespace Optikpi\DataPipeline\Models;

/**
 * System Event Model
 * Represents system-related events (e.g., campaign triggers, system notifications)
 */
class SystemEvent
{
    public $account_id;
    public $workspace_id;
    public $event_category;
    public $event_name;
    public $event_id;
    public $event_time;
    public $event_data;

    /**
     * Constructor
     *
     * @param array $data System event data
     */
    public function __construct(array $data = [])
    {
        $this->event_category = $data['event_category'] ?? 'SystemEvent';
        $unknownFields = [];
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
                $this->$key = $value;
            } else {
                $unknownFields[] = $key;
            }
        }
        if (!empty($unknownFields)) {
            throw new \InvalidArgumentException(
                'Unknown field(s): ' . implode(', ', $unknownFields)
                . '. Valid fields are: ' . implode(', ', array_keys(get_object_vars($this)))
            );
        }
    }

    /**
     * Validates the system event data
     *
     * @return array Validation result with isValid boolean and errors array
     */
    public function validate(): array
    {
        $errors = [];

        // Required fields
        if (empty($this->account_id)) {
            $errors[] = 'account_id is required';
        }
        if (empty($this->workspace_id)) {
            $errors[] = 'workspace_id is required';
        }
        if (empty($this->event_category)) {
            $errors[] = 'event_category is required';
        }
        if (empty($this->event_name)) {
            $errors[] = 'event_name is required';
        }
        if (empty($this->event_id)) {
            $errors[] = 'event_id is required';
        }
        if (empty($this->event_time)) {
            $errors[] = 'event_time is required';
        }
        if ($this->event_data === null) {
            $errors[] = 'event_data is required';
        } elseif (!is_string($this->event_data) && !is_array($this->event_data)) {
            $errors[] = 'event_data must be a string or an object';
        }

        // Event category validation
        if (!empty($this->event_category) && $this->event_category !== 'SystemEvent') {
            $errors[] = 'event_category must be "SystemEvent" for system events';
        }

        // Event name validation
        $validEventNames = [
            'Campaign Trigger',
            'Manual Action'
        ];

        if (!empty($this->event_name) && !in_array($this->event_name, $validEventNames)) {
            $errors[] = 'event_name must be one of: ' . implode(', ', $validEventNames);
        }

        // Date format validation
        if (!empty($this->event_time) && !$this->isValidDateTime($this->event_time)) {
            $errors[] = 'event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)';
        }

        return [
            'isValid' => empty($errors),
            'errors' => $errors
        ];
    }

    /**
     * Validates ISO 8601 datetime format
     *
     * @param string $dateTime DateTime string to validate
     * @return bool True if datetime is valid
     */
    private function isValidDateTime(string $dateTime): bool
    {
        try {
            $date = new \DateTime($dateTime);
            return $date !== false;
        } catch (\Exception $e) {
            return false;
        }
    }

    /**
     * Converts the model to a plain array
     *
     * @return array Plain array representation
     */
    public function toArray(): array
    {
        $arr = [];
        foreach (get_object_vars($this) as $key => $value) {
            if ($value !== null) {
                $arr[$key] = $value;
            }
        }
        return $arr;
    }

    /**
     * Creates a SystemEvent instance from array
     *
     * @param array $data Plain array data
     * @return SystemEvent New SystemEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}
