package com.intuit.accountingprofileservice.adapter.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.intuit.accountingprofileservice.domain.AccountingProfile;

public class AccountingProfileValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AccountingProfile.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taxId", "TaxIdEmpty",
				"Tax Id cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NameEmpty",
				"Name cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailId", "EmailIdEmpty",
				"Email Id cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "clientType", "ClientTypeEmpty",
				"Client Type cannot be empty");
		
	}

}
