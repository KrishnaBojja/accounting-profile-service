package com.intuit.accountingprofileservice.adapter.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.accountingprofileservice.adapter.validator.AccountingProfileValidator;
import com.intuit.accountingprofileservice.domain.AccountingProfile;
import com.intuit.accountingprofileservice.domain.repository.AccountingProfileRepository;

@RestController
@RequestMapping("/private/intuit")
public class AccountingProfileServiceController {
	
	@Autowired
	private AccountingProfileRepository accountingProfileRepository;

	
	@GetMapping(value = "/accountingInfo/{taxId}")
	public ResponseEntity<AccountingProfile> getClientDetailsByTaxId(@PathVariable("taxId") Long taxId) {
		
		AccountingProfile accountingProfile = accountingProfileRepository.getAccountingProfileByTaxId(taxId);
		if(accountingProfile == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(accountingProfile, HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "/accountingInfo")
	public ResponseEntity<?> createAccountInfo(
			@RequestHeader(name = "correlationId") String correlationId, 
			@Valid @RequestBody AccountingProfile accountingProfile, BindingResult result){

		
		if(result.hasErrors()) {
			return new ResponseEntity<>(String.join(",",result.getAllErrors().stream()
					.collect(Collectors.mapping(ObjectError::getDefaultMessage, Collectors.toList())) ),
					HttpStatus.BAD_REQUEST);
		}
		if(accountingProfileRepository.getAccountingProfileByTaxId(accountingProfile.getTaxId())!=null) {
			return new ResponseEntity<>("Accounting Information all ready created",HttpStatus.BAD_REQUEST);
		}
		AccountingProfile response = accountingProfileRepository.insert(accountingProfile);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
		
	}
	
	@GetMapping(value = "/accountingInfo/clients")
	public ResponseEntity<List<AccountingProfile>> getClientDetailsByClientType(@RequestParam("accountType") String accountType) {
		
		List<AccountingProfile> accountingProfiles = accountingProfileRepository.getAccountingProfileByClientType(accountType);
		if(accountingProfiles.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(accountingProfiles, HttpStatus.OK);
		}
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder ) {
		binder.setValidator(new AccountingProfileValidator());
		
	}
	
	

}
