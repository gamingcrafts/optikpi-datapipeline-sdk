package com.optikpi.datapipeline.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerProfileTest {

    private CustomerProfile customer;

    @BeforeEach
    void setUp() {
        customer = new CustomerProfile();
    }

    @Test
    void testValidCustomerProfile() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        customer.setAccountStatus("Active");
        customer.setVipStatus("Regular");
        
        ValidationResult result = customer.validate();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testInvalidCustomerProfileMissingRequiredFields() {
        ValidationResult result = customer.validate();
        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("account_id is required"));
        assertTrue(result.getErrors().contains("workspace_id is required"));
        assertTrue(result.getErrors().contains("user_id is required"));
        assertTrue(result.getErrors().contains("username is required"));
        assertTrue(result.getErrors().contains("email is required"));
    }

    @Test
    void testInvalidEmailFormat() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("invalid-email");
        
        ValidationResult result = customer.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("email must be a valid email address"));
    }

    @Test
    void testValidEmailFormats() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_name@sub.example.com"
        };
        
        for (String email : validEmails) {
            customer.setEmail(email);
            ValidationResult result = customer.validate();
            assertTrue(result.isValid(), "Email " + email + " should be valid");
        }
    }

    @Test
    void testInvalidDateOfBirth() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        customer.setDateOfBirth("invalid-date");
        
        ValidationResult result = customer.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("date_of_birth must be in YYYY-MM-DD format"));
    }

    @Test
    void testInvalidGender() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        customer.setGender("Invalid");
        
        ValidationResult result = customer.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("gender must be one of: Male, Female, Other"));
    }

    @Test
    void testInvalidAccountStatus() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        customer.setAccountStatus("Invalid");
        
        ValidationResult result = customer.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("account_status must be one of: Active, Inactive, Suspended, Closed"));
    }

    @Test
    void testInvalidVipStatus() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        customer.setVipStatus("Invalid");
        
        ValidationResult result = customer.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond"));
    }

    @Test
    void testValidDateOfBirth() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        customer.setDateOfBirth("1990-01-15");
        
        ValidationResult result = customer.validate();
        assertTrue(result.isValid());
    }

    @Test
    void testValidDateOfBirthFormats() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        
        String[] validDates = {
            "1990-01-15",
            "2000-12-31",
            "1985-06-20"
        };
        
        for (String date : validDates) {
            customer.setDateOfBirth(date);
            ValidationResult result = customer.validate();
            assertTrue(result.isValid(), "Date " + date + " should be valid");
        }
    }

    @Test
    void testValidGenderValues() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        
        String[] validGenders = {"Male", "Female", "Other"};
        for (String gender : validGenders) {
            customer.setGender(gender);
            ValidationResult result = customer.validate();
            assertTrue(result.isValid(), "Gender " + gender + " should be valid");
        }
    }

    @Test
    void testValidAccountStatusValues() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        
        String[] validStatuses = {"Active", "Inactive", "Suspended", "Closed"};
        for (String status : validStatuses) {
            customer.setAccountStatus(status);
            ValidationResult result = customer.validate();
            assertTrue(result.isValid(), "Account status " + status + " should be valid");
        }
    }

    @Test
    void testValidVipStatusValues() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        
        String[] validVipStatuses = {"Regular", "Silver", "Gold", "Platinum", "Diamond"};
        for (String vipStatus : validVipStatuses) {
            customer.setVipStatus(vipStatus);
            ValidationResult result = customer.validate();
            assertTrue(result.isValid(), "VIP status " + vipStatus + " should be valid");
        }
    }

    @Test
    void testConstructorWithParameters() {
        CustomerProfile customer = new CustomerProfile("acc_12345", "ws_67890", "user_001", "john_doe", "john.doe@example.com");
        
        assertEquals("acc_12345", customer.getAccountId());
        assertEquals("ws_67890", customer.getWorkspaceId());
        assertEquals("user_001", customer.getUserId());
        assertEquals("john_doe", customer.getUsername());
        assertEquals("john.doe@example.com", customer.getEmail());
    }

    @Test
    void testDefaultConstructor() {
        CustomerProfile customer = new CustomerProfile();
        
        assertNotNull(customer);
        // All fields should be null initially
        assertEquals(null, customer.getAccountId());
        assertEquals(null, customer.getWorkspaceId());
        assertEquals(null, customer.getUserId());
    }

    @Test
    void testSettersAndGetters() {
        customer.setAccountId("acc_test");
        customer.setWorkspaceId("ws_test");
        customer.setUserId("user_test");
        customer.setUsername("testuser");
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setDateOfBirth("1990-01-01");
        customer.setGender("Male");
        customer.setCountry("US");
        customer.setCity("New York");
        customer.setAccountStatus("Active");
        customer.setVipStatus("Gold");
        
        assertEquals("acc_test", customer.getAccountId());
        assertEquals("ws_test", customer.getWorkspaceId());
        assertEquals("user_test", customer.getUserId());
        assertEquals("testuser", customer.getUsername());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("1990-01-01", customer.getDateOfBirth());
        assertEquals("Male", customer.getGender());
        assertEquals("US", customer.getCountry());
        assertEquals("New York", customer.getCity());
        assertEquals("Active", customer.getAccountStatus());
        assertEquals("Gold", customer.getVipStatus());
    }

    @Test
    void testValidationResultStructure() {
        customer.setAccountId("acc_12345");
        customer.setWorkspaceId("ws_67890");
        customer.setUserId("user_001");
        customer.setUsername("john_doe");
        customer.setEmail("john.doe@example.com");
        
        ValidationResult result = customer.validate();
        
        assertNotNull(result);
        assertTrue(result.isValid());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testMultipleValidationErrors() {
        // Create customer with multiple validation errors
        customer.setEmail("invalid-email");
        customer.setGender("Invalid");
        customer.setAccountStatus("Invalid");
        customer.setVipStatus("Invalid");
        customer.setDateOfBirth("invalid-date");
        
        ValidationResult result = customer.validate();
        
        assertFalse(result.isValid());
        // Should have errors for: required fields (5) + invalid values (5) = 10 errors
        assertTrue(result.getErrors().size() >= 5, "Should have at least 5 errors");
    }
}