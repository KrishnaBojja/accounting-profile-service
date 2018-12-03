package com.intuit.accountingprofileservice.domain.repository;

import java.util.List;

import com.intuit.accountingprofileservice.domain.AccountingProfile;

public interface AccountingProfileRepository {
	
	AccountingProfile insert(AccountingProfile accountingProfile);
	
	AccountingProfile getAccountingProfileByTaxId(long taxId);
	
	List<AccountingProfile> getAccountingProfileByClientType(String clientType);
	
	

}
