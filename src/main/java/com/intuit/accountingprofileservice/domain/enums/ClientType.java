package com.intuit.accountingprofileservice.domain.enums;

import java.util.Arrays;

public enum ClientType {
	Individual("Individual"),
	Business("Business");
	
	private final String code;
	
	ClientType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static ClientType getClientTypeByCode(String clientTypeCode) {
		return Arrays.stream(ClientType.values())
				.filter(clientType-> clientType.getCode().equals(clientTypeCode))
				.findFirst().orElse(null);
	}

}
