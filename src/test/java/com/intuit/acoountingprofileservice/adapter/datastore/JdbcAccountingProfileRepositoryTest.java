package com.intuit.acoountingprofileservice.adapter.datastore;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.intuit.accountingprofileservice.adapter.datastore.JdbcAccountingProfileRepository;
import com.intuit.accountingprofileservice.app.Application;
import com.intuit.accountingprofileservice.domain.AccountingProfile;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public class JdbcAccountingProfileRepositoryTest {
	
	@Autowired
	private JdbcTemplate writeJdbcTemplate;
	
	@Autowired
	private JdbcAccountingProfileRepository subject;
	
	private AccountingProfile accountingProfile;
	
	private static final String DELETE_ACCOUNTING_PROFILE = 
			"DELETE FROM ACCOUNTING_PROFILE WHERE client_type = ?";
	
	@Before
	public void before() {
		accountingProfile = createAccountingProfileRecord();
		writeJdbcTemplate.update(DELETE_ACCOUNTING_PROFILE,"clientType");
	}
	
	@Test
	public void insert_accountingProfileValidRecord_returnsAccountingProfile() {
		AccountingProfile actual = subject.insert(accountingProfile);
		assertThat(actual.getTaxId(), is(1234L));
	}
	
	@Test
	public void insert_accountingProfileInvalidRecord_returnsNull() {
		accountingProfile.setClientType(null);
		AccountingProfile actual = subject.insert(accountingProfile);
		assertThat(actual, is(nullValue()));
	}
	
	@Test
	public void getAccountingProfileByTaxId_existingRecord_returnsAccountingProfile() {
		subject.insert(accountingProfile);
		AccountingProfile actual = subject.getAccountingProfileByTaxId(1234L);
		assertThat(actual.getTaxId(), is(1234L));
	}
	
	@Test
	public void getAccountingProfileByTaxId_nonexistingRecord_returnsNull() {
		AccountingProfile actual = subject.getAccountingProfileByTaxId(1234L);
		assertThat(actual, is(nullValue()));
	}
	
	@Test
	public void getAccountingProfileByAccountType_existingRecords_returnsAccountingProfiles() {
		subject.insert(accountingProfile);
		accountingProfile.setTaxId(1235L);
		subject.insert(accountingProfile);
		List<AccountingProfile> actual = subject.getAccountingProfileByClientType("clientType");
		assertThat(actual.size(), is(2));
	}
	
	@Test
	public void getAccountingProfileByAccountType_noRecords_returnsEmpty() {
		List<AccountingProfile> actual = subject.getAccountingProfileByClientType("clientType");
		assertThat(actual.size(), is(0));
	}

	private AccountingProfile createAccountingProfileRecord() {
		AccountingProfile accountingProfile = new AccountingProfile();
		accountingProfile.setTaxId(1234L);
		accountingProfile.setName("name");
		accountingProfile.setEmailId("emailId");
		accountingProfile.setClientType("clientType");
		return accountingProfile;
	}
	

}
