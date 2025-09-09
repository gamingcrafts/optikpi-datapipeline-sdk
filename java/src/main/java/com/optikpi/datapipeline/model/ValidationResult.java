package com.optikpi.datapipeline.model;

import java.util.List;

/**
 * Validation result for model validation
 */
public class ValidationResult {
    private boolean isValid;
    private List<String> errors;
    
    public ValidationResult() {}
    
    public ValidationResult(boolean isValid, List<String> errors) {
        this.isValid = isValid;
        this.errors = errors;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public void setValid(boolean valid) {
        isValid = valid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
