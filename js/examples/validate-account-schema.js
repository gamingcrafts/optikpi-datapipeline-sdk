const Ajv = require('ajv');
const addFormats = require('ajv-formats');
const fs = require('fs');
const path = require('path');

// Import the test data
const { ACCOUNT_EVENT } = require('./test-account-endpoint');

// Load the account schema
const schemaPath = path.join(__dirname, '../api-to-s3/jsons/account-schema.json');
const accountSchema = JSON.parse(fs.readFileSync(schemaPath, 'utf8'));

// Initialize Ajv with formats
const ajv = new Ajv({ allErrors: true });
addFormats(ajv);

// Validate the test data
function validateAccountData() {
  console.log('🔍 Validating Account Event Data against Schema');
  console.log('==============================================');
  
  console.log('\n📋 Test Data:');
  console.log(JSON.stringify(ACCOUNT_EVENT, null, 2));
  
  console.log('\n📋 Schema Requirements:');
  console.log('Required fields:', accountSchema.required);
  console.log('Event names allowed:', accountSchema.properties.event_name.enum);
  console.log('Device types allowed:', accountSchema.properties.device.enum);
  console.log('Status values allowed:', accountSchema.properties.status.enum);
  
  // Validate against schema
  const validate = ajv.compile(accountSchema);
  const isValid = validate(ACCOUNT_EVENT);
  
  console.log('\n✅ Validation Results:');
  console.log('==============================================');
  
  if (isValid) {
    console.log('🎉 PASS: Test data is valid according to schema!');
    
    // Additional checks
    console.log('\n📊 Data Analysis:');
    console.log(`- Total fields: ${Object.keys(ACCOUNT_EVENT).length}`);
    console.log(`- Required fields present: ${accountSchema.required.length}/${accountSchema.required.length}`);
    
    // Check required fields
    const missingRequired = accountSchema.required.filter(field => !ACCOUNT_EVENT.hasOwnProperty(field));
    if (missingRequired.length === 0) {
      console.log('✅ All required fields are present');
    } else {
      console.log(`❌ Missing required fields: ${missingRequired.join(', ')}`);
    }
    
    // Check enum values
    const eventNameValid = accountSchema.properties.event_name.enum.includes(ACCOUNT_EVENT.event_name);
    const deviceValid = !ACCOUNT_EVENT.device || accountSchema.properties.device.enum.includes(ACCOUNT_EVENT.device);
    const statusValid = !ACCOUNT_EVENT.status || accountSchema.properties.status.enum.includes(ACCOUNT_EVENT.status);
    
    console.log(`✅ Event name valid: ${eventNameValid}`);
    console.log(`✅ Device type valid: ${deviceValid}`);
    console.log(`✅ Status valid: ${statusValid}`);
    
  } else {
    console.log('❌ FAIL: Test data has validation errors!');
    console.log('\n🚨 Validation Errors:');
    validate.errors.forEach((error, index) => {
      console.log(`${index + 1}. ${error.instancePath || 'root'}: ${error.message}`);
      if (error.params) {
        console.log(`   Details:`, error.params);
      }
    });
  }
  
  return isValid;
}

// Run validation
if (require.main === module) {
  validateAccountData();
}

module.exports = {
  validateAccountData,
  accountSchema
}; 