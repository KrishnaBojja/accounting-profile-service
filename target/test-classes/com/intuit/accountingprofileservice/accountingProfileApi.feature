Feature: AccountingProfile api endpoints
	@Fast
	@dev @qa
	Scenario Outline: Get Accounting Profile by Tax Id
		Given the test records are added to the database
		When the API is called for tax id <taxId>
		Then the API returns name <name>
		And the API returns email id <emailId>
		And the API returns client type <clientType>
		Examples:
			|taxId     |name     |emailId        |clientType    |
			|"111111"   |"agent"  |"abc@gmail.com"|"Individual"  |
			
	@dev @qa
	Scenario: Get Accounting Profile by invalid tax id
		Given the test records are added to the database
		When the API is called for invalid tax id 1111
		Then response should return 404
		
	@dev @qa
	Scenario Outline: Create Accounting Portfolio
		Given the test records are added to the database
		When the API is called for accounting profile with taxId <taxId> and name <name> and emailId <emailId> and clientType <clientType>
		Then the API returns name <name>
		And the API returns emailId <emailId> 
		And the API returns clientType <clientType>
		Examples:
			|taxId       |name        |emailId                  |clientType           |
			|"222222"    |"intuit"     |"def@gmail.com"         |"Business"           |
 
 	@dev @qa
	Scenario Outline: Create an invalid Accounting Portfolio post request
		Given the test records are added to the database
		When the API is called for invalid parameters with taxId <taxId> and name <name> and emailId <emailId> and clientType <clientType>
		Then the API returns error message <errorMessage>
		Examples:
			|taxId       |name        |emailId                  |clientType           |errorMessage                 |
			|"111111"    |""          |"def@gmail.com"          |"Business"           |"Name cannot be empty"       |
			|""          |"abc"       |"abc@gmail.com"          |"Business"           |"Tax Id cannot be empty"     |
			|"111111"    |"abc"       |""                       |"Individual"         |"Email Id cannot be empty"   |
			|"111111"    |"abc"       |"abc@gmail.com"          |""                   |"Client Type cannot be empty,ClientType value should be either Individual or Business"|
			|"111111"    |"abc"       |"abc@gmail.com"          |"clientType"         |"ClientType value should be either Individual or Business"|
			
    @dev @qa
	Scenario Outline: Get Accounting Portfolio by client type
		Given the test records are added to the database
		When the API is called for accounting profile with clientType <clientType>
		Then the API returns all taxids <taxId>
		Examples:
			|taxId           |clientType|
			|"333333,444444" |"Business"|
			