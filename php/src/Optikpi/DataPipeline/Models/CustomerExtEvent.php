<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Customer Extension Event Model
 * Represents customer extended attributes events for the Data Pipeline API
 */
class CustomerExtEvent
{
    public $account_id;
    public $workspace_id;
    public $user_id;
    public $list_name;
    public $ext_data;

    /**
     * Constructor
     *
     * @param array $data Customer extension event data
     */
    public function __construct(array $data = [])
    {
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
                $this->$key = $value;
            }
        }
    }

    /**
     * Validates the customer extension event data
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
        if (empty($this->user_id)) {
            $errors[] = 'user_id is required';
        }
        if (empty($this->list_name)) {
            $errors[] = 'list_name is required';
        }
        if ($this->ext_data === null || $this->ext_data === '') {
            $errors[] = 'ext_data is required';
        }

        // Validate ext_data format
        if ($this->ext_data !== null) {
            if (is_string($this->ext_data)) {
                $decoded = json_decode($this->ext_data, true);
                if (json_last_error() !== JSON_ERROR_NONE) {
                    $errors[] = 'ext_data must be a valid JSON string or object';
                }
            } elseif (!is_array($this->ext_data) && !is_object($this->ext_data)) {
                $errors[] = 'ext_data must be an object or JSON string';
            }
        }

        // Validate list_name format (alphanumeric, underscores, hyphens)
        if (!empty($this->list_name) && !preg_match('/^[A-Za-z0-9_-]+$/', $this->list_name)) {
            $errors[] = 'list_name must contain only alphanumeric characters, underscores, and hyphens';
        }

        // Validate user_id format
        if (!empty($this->user_id) && !is_string($this->user_id)) {
            $errors[] = 'user_id must be a string';
        }

        return [
            'isValid' => empty($errors),
            'errors' => $errors
        ];
    }

    /**
     * Ensures ext_data is in the correct format for API submission
     * Converts object to JSON string if needed
     *
     * @return array Event array with properly formatted ext_data
     */
    public function toAPIFormat(): array
    {
        $formatted = $this->toArray();

        // Convert ext_data to JSON string if it's an object/array
        if (is_array($formatted['ext_data']) || is_object($formatted['ext_data'])) {
            $formatted['ext_data'] = json_encode($formatted['ext_data'], JSON_UNESCAPED_SLASHES);
        }

        return $formatted;
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
     * Creates a CustomerExtEvent instance from array
     *
     * @param array $data Plain array data
     * @return CustomerExtEvent New CustomerExtEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }

    /**
     * Helper method to create a customer extension event with object ext_data
     *
     * @param array $params Event parameters
     * @return CustomerExtEvent New CustomerExtEvent instance
     */
    public static function createWithObject(array $params): self
    {
        return new self([
            'account_id' => $params['account_id'],
            'workspace_id' => $params['workspace_id'],
            'user_id' => $params['user_id'],
            'list_name' => $params['list_name'],
            'ext_data' => $params['ext_data'] // Object format
        ]);
    }

    /**
     * Helper method to create a customer extension event with JSON string ext_data
     *
     * @param array $params Event parameters
     * @return CustomerExtEvent New CustomerExtEvent instance
     */
    public static function createWithString(array $params): self
    {
        return new self([
            'account_id' => $params['account_id'],
            'workspace_id' => $params['workspace_id'],
            'user_id' => $params['user_id'],
            'list_name' => $params['list_name'],
            'ext_data' => $params['ext_data'] // JSON string format
        ]);
    }
}

