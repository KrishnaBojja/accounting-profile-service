package com.intuit.accountingprofileservice.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.assertj.core.util.Arrays;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.intuit.accountingprofileservice.app.Application;
import com.intuit.accountingprofileservice.domain.AccountingProfile;
import com.intuit.accountingprofileservice.util.ServerUri;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@Ignore
public class AccountingProfileApiTestSteps {
	
	private static final String BASE_RESOURCE = "/private/intuit";
	private static final String CORRELATION_ID = "correlationId";
	private static final String RESOURCE_URI = "/accountingInfo";
	private static final String TYPE_URI = "/clients";
	
	private HttpHeaders requestHeaders;
	private HttpEntity<String> request;
	private RestTemplate restTemplate;
	private AccountingProfile accountingProfile;
	private AccountingProfile[] accountingProfiles;
	private ResponseEntity<?> getAccountingByIdResponse;
	private ResponseEntity<?> accountingProfileResponse;
	private int statusCode;
	private String validationErrorMessage;
	
	@Autowired
	private ServerUri serverUri;
	
	@Autowired
	private DataSource writeDataSource;
	
	@Value("classpath:data/api/clear-test-data.sql")
	private Resource clearAccountingApiData;
	
	@Value("classpath:data/api/add-test-data.sql")
	private Resource addAccountingApiData;
	
	@Before
	public void before() {
		requestHeaders = new HttpHeaders();
		requestHeaders.add(CORRELATION_ID, UUID.randomUUID().toString());
		request = new HttpEntity<String>(requestHeaders);
		restTemplate = new RestTemplate();
	}
	
	@Given("^the test records are added to the database$")
	public void theTestRecordsAreAddedToTheDatabase() {
		clearTestData();
		addTestData();
	}
	
	@When("^the API is called for tax id \"([^\"]*)\"$")
	public void theApiIsCalledForTaxId(String taxId) {
		getAccountingByIdResponse = restTemplate.exchange(accountingUriBuilder(RESOURCE_URI, taxId),
				HttpMethod.GET, request, AccountingProfile.class);
		accountingProfile = (AccountingProfile) getAccountingByIdResponse.getBody();
	}
	
	@When("^the API is called for invalid tax id (\\d+)$")
	public void theApiIsCalledForInvalidTaxId(int taxId) {
		try {
			getAccountingByIdResponse = restTemplate.exchange(accountingUriBuilder(RESOURCE_URI, Integer.toString(taxId)),
					HttpMethod.GET, request, AccountingProfile.class);
		} catch (HttpClientErrorException e) {
			statusCode = e.getRawStatusCode();
		}
	}
	
	@When("^the API is called for accounting profile with taxId \"([^\"]*)\" and name \"([^\"]*)\" and emailId \"([^\"]*)\" and clientType \"([^\"]*)\"$")
	public void theAPIIsCalledForAccountingProfileWithTaxIdAndNameAndEmailIdAndClientType(
			String taxId, String name, String emailId, String clientType) {
		Map<String, Object> body = new HashMap<>();
		body.put("taxId", taxId);
		body.put("name", name);
		body.put("emailId", emailId);
		body.put("clientType", clientType);
		
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> postRequest = new HttpEntity<Object>(body,requestHeaders);
		accountingProfileResponse = restTemplate.exchange(accountingUriBuilder(RESOURCE_URI, null),
				HttpMethod.POST, postRequest, AccountingProfile.class);
		accountingProfile = (AccountingProfile) accountingProfileResponse.getBody();		
	}
	
	@When("^the API is called for invalid parameters with taxId \"([^\"]*)\" and name \"([^\"]*)\" and emailId \"([^\"]*)\" and clientType \"([^\"]*)\"$")
	public void theAPIIsCalledForInvalidParametersWithTaxIdAndNameAndEmailIdAndClientType(
			String taxId, String name, String emailId, String clientType) {
		Map<String, Object> body = new HashMap<>();
		body.put("taxId", taxId);
		body.put("name", name);
		body.put("emailId", emailId);
		body.put("clientType", clientType);
		
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> postRequest = new HttpEntity<Object>(body,requestHeaders);
		try {
			accountingProfileResponse = restTemplate.exchange(accountingUriBuilder(RESOURCE_URI, null),
					HttpMethod.POST, postRequest, AccountingProfile.class);
		} catch (HttpClientErrorException e) {
			validationErrorMessage = e.getResponseBodyAsString();
		}
	}
	
	@When("^the API is called for accounting profile with clientType \"([^\"]*)\"$")
	public void theApiIsCalledForAccountProfileWithClientType(String clientType) {
		getAccountingByIdResponse = restTemplate.exchange(accountingUriQueryBuilder(RESOURCE_URI+TYPE_URI, clientType),
				HttpMethod.GET, request, AccountingProfile[].class);
		accountingProfiles = (AccountingProfile[]) getAccountingByIdResponse.getBody();
	}
	
	@Then("^response should return (\\d+)$")
	public void responseShouldReturn(int statusCode) {
		assertThat(this.statusCode, is(statusCode));
	}
	
	@Then("^the API returns name \"([^\"]*)\"$")
	public void theAPIReturnsName(String name) {
		assertThat(accountingProfile.getName(), is(name));
	}
	
	@Then("^the API returns emailId \"([^\"]*)\"$")
	public void theAPIReturnsEmailId(String emailId) {
		assertThat(accountingProfile.getEmailId(), is(emailId));
	}
	
	@Then("^the API returns clientType \"([^\"]*)\"$")
	public void theAPIReturnsClientType(String clientType) {
		assertThat(accountingProfile.getClientType(), is(clientType));
	}
	
	@Then("^the API returns error message \"([^\"]*)\"$")
	public void theAPIReturnsErrorMessage(String errorMessages) {
		assertThat(validationErrorMessage, is(errorMessages));
	}
	
	@Then("^the API returns all taxids \"([^\"]*)\"$")
	public void theAPIReturnsForAllTaxIds(String taxId) {
		String[] taxIds = taxId.split(",");
		List<Object> taxIdList = Arrays.asList(taxIds);
		int count = 0;
		for (AccountingProfile accountingProfile : accountingProfiles) {
			if(accountingProfile.getName().equals("intuit")) {
				assertThat(taxIdList.contains(Long.toString(accountingProfile.getTaxId())), is(true));
				count++;
			}
		}
		assertThat(count, is(2));
		
	}
	
	private String accountingUriBuilder(String resourceUri, String param) {
		return UriComponentsBuilder.fromUri(serverUri.getUri()).path(BASE_RESOURCE+resourceUri)
				.pathSegment(param).build().toUriString();
	}
	
	private String accountingUriQueryBuilder(String resourceUri, String param) {
		return UriComponentsBuilder.fromUri(serverUri.getUri()).path(BASE_RESOURCE+resourceUri)
				.queryParam("clientType", param).build().toUriString();
	}

	private void addTestData() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(addAccountingApiData);
		populator.execute(writeDataSource);
	}

	private void clearTestData() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearAccountingApiData);
		populator.execute(writeDataSource);
	}

	

}
