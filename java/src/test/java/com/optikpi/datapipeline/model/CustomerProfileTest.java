package com.optikpi.datapipeline.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
