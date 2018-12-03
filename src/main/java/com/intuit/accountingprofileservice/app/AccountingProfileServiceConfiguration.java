package com.intuit.accountingprofileservice.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AccountingProfileServiceConfiguration {
	
	@Value("${datasource.write.driverClassName}")
	private String driverClassName;
	
	@Value("${datasource.write.url}")
	private String url;
	
	@Value("${datasource.write.username}")
	private String username;
	
	@Value("${datasource.write.password}")
	private String password;
	
	@Bean
	public DataSource writeDataSource() {
		return DataSourceBuilder.create().driverClassName(driverClassName).url(url)
				.username(username).password(password).build();
	}
	
	@Bean
	public JdbcTemplate writeJdbcTemplate() {
		return new JdbcTemplate(writeDataSource());
	}
	
	@Bean
	public JdbcTemplate readJdbcTemplate() {
		return new JdbcTemplate(writeDataSource());
	}

}
