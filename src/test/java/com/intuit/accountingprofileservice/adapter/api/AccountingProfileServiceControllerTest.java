package com.intuit.accountingprofileservice.adapter.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;

import com.intuit.accountingprofileservice.adapter.api.AccountingProfileServiceController;
import com.intuit.accountingprofileservice.adapter.validator.AccountingProfileValidator;
import com.intuit.accountingprofileservice.domain.AccountingProfile;
import com.intuit.accountingprofileservice.domain.repository.AccountingProfileRepository;

@RunWith(MockitoJUnitRunner.class)
public class AccountingProfileServiceControllerTest {
	
	@Mock
	private AccountingProfileRepository accountingProfileRepository;
	@Mock
	private AccountingProfile accountingProfile;
	@InjectMocks
	private AccountingProfileServiceController subject;
	
	private String correlationId;
	
	
	@Before
	public void before() {
		
		correlationId = "testCorrelationId";
		when(accountingProfileRepository.getAccountingProfileByTaxId(anyLong())).thenReturn(accountingProfile);
		when(accountingProfileRepository.getAccountingProfileByClientType(anyString())).thenReturn(Arrays.asList(accountingProfile));
		when(accountingProfileRepository.insert(anyObject())).thenReturn(accountingProfile);
	}
	
	@Test
	public void getClientDetailsByTaxId_callWithExistingTaxId_returnsAccountingInfo() {
		ResponseEntity<AccountingProfile> actual = subject.getClientDetailsByTaxId(1234L);
		assertThat(actual.getBody(), is(equalTo(accountingProfile)));
	}
	
	@Test
	public void getClientDetailsByTaxId_callWithNotExistingTaxId_returnsAccountingInfo() {
		when(accountingProfileRepository.getAccountingProfileByTaxId(anyLong())).thenReturn(null);
		ResponseEntity<AccountingProfile> actual = subject.getClientDetailsByTaxId(1234L);
		assertThat(actual.getStatusCodeValue(), is(equalTo(404)));
	}
	
	@Test
	public void getClientDetailsByClientType_callWithExistingClientType_returnsAccountingInfo() {
		ResponseEntity<List<AccountingProfile>> actual = subject.getClientDetailsByClientType("Individual");
		assertTrue(actual.getBody().contains(accountingProfile));
	}
	
	@Test
	public void getAccountingProfileByTaxId_callWithNotExistingClientType_returnsAccountingInfo() {
		when(accountingProfileRepository.getAccountingProfileByClientType(anyString())).thenReturn(new ArrayList<AccountingProfile>());
		ResponseEntity<List<AccountingProfile>> actual = subject.getClientDetailsByClientType("Individual");
		assertThat(actual.getStatusCodeValue(), is(equalTo(404)));
	}
	
	@Test
	public void createAccountInfo_callWithNewClient_returnsAccountingInfo() {
		BindingResult mockBindingresult = mock(BindingResult.class);
		when(accountingProfileRepository.getAccountingProfileByTaxId(anyLong())).thenReturn(null);
		ResponseEntity<AccountingProfile> actual = (ResponseEntity<AccountingProfile>) subject.createAccountInfo("correlationId", accountingProfile, mockBindingresult);
		assertThat(actual.getBody(), is(equalTo(accountingProfile)));
	}
	
	@Test
	public void createAccountInfo_callWithExistingClient_returnsError() {
		BindingResult mockBindingresult = mock(BindingResult.class);
		when(accountingProfileRepository.getAccountingProfileByTaxId(anyLong())).thenReturn(accountingProfile);
		ResponseEntity<?> actual = subject.createAccountInfo("correlationId", accountingProfile, mockBindingresult);
		assertThat(actual.getStatusCodeValue(), is(equalTo(400)));
	}
	
	@Test
	public void createAccountInfo_callWithBlankTaxId__returnsError() {
		BindingResult mockBindingresult = mock(BindingResult.class);
		when(mockBindingresult.hasErrors()).thenReturn(true);
		List<ObjectError> errorList = new ArrayList<>();
		errorList.add(new ObjectError("taxId","TaxId can't be empty"));
		when(mockBindingresult.getAllErrors()).thenReturn(errorList);
		ResponseEntity<?> actual = subject.createAccountInfo("correlationId", accountingProfile, mockBindingresult);
		assertThat(actual.getStatusCodeValue(), is(equalTo(400)));
	}
	
	@Test
	public void initBinder_callWithBinder_setsValidator() {
		String test = null;
		WebDataBinder dataBinder = new WebDataBinder(test,"test");
		subject.initBinder(dataBinder);
		assertThat(dataBinder.getValidator(), is(instanceOf(AccountingProfileValidator.class)));
	}

}
