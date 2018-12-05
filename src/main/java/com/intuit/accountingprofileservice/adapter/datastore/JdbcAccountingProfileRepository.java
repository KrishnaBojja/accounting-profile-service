package com.intuit.accountingprofileservice.adapter.datastore;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.intuit.accountingprofileservice.domain.AccountingProfile;
import com.intuit.accountingprofileservice.domain.repository.AccountingProfileRepository;

@Repository
public class JdbcAccountingProfileRepository implements AccountingProfileRepository {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(JdbcAccountingProfileRepository.class);
	
	private static final String ACCOUNTING_PROFILE_TABLE = "accounting_profile";
	private static final String GET_ACCOUNTING_PROFILE_BY_TAX_ID = 
			"SELECT * FROM accounting_profile WHERE tax_id = ?";
	private static final String GET_ALL_ACCOUNTING_PROFILE_BY_CLIENT_TYPE = 
			"SELECT * FROM accounting_profile WHERE client_type = ?";

	
	@Autowired
	private JdbcTemplate readJdbcTemplate;
	
	@Autowired
	private DataSource writeDataSource;
	
	private SimpleJdbcInsert simpleJdbcInsert;
	
	@Value("${liquibase.default-schema:Public}")
	private String schemaName;
	
	@PostConstruct
	public void init() {
		simpleJdbcInsert = new SimpleJdbcInsert(writeDataSource).withSchemaName(schemaName)
				.withTableName(ACCOUNTING_PROFILE_TABLE);
	}

	@Override
	public AccountingProfile insert(AccountingProfile accountingProfile) {
		accountingProfile.setCreatedTimestamp(LocalDateTime.now());
		accountingProfile.setUpdatedTimestamp(LocalDateTime.now());
		BeanPropertySqlParameterSource parameters = 
				new BeanPropertySqlParameterSource(accountingProfile);
		try {
			simpleJdbcInsert.execute(parameters);
		} catch (Exception e) {
			LOGGER.error("Error inserting accounting profile for tax_id: {}",
					accountingProfile.getTaxId(),e.getMessage());
			return null;
		}
		return accountingProfile;
	}

	@Override
	public AccountingProfile getAccountingProfileByTaxId(long taxId) {
		try {
			return readJdbcTemplate.queryForObject(GET_ACCOUNTING_PROFILE_BY_TAX_ID, 
					new Object[] {taxId},
					new BeanPropertyRowMapper<>(AccountingProfile.class));
		} catch (DataAccessException e) {
			LOGGER.info("Error fetching accounting profile for tax_id: {}",
					taxId,e.getMessage());
			return null;
		}
	}

	@Override
	public List<AccountingProfile> getAccountingProfileByClientType(String clientType) {
		return readJdbcTemplate.query(GET_ALL_ACCOUNTING_PROFILE_BY_CLIENT_TYPE,
				new Object[] {clientType},
				new BeanPropertyRowMapper<>(AccountingProfile.class));

	}

}
