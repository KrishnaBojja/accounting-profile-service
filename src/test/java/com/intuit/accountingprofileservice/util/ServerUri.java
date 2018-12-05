package com.intuit.accountingprofileservice.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ServerUri implements ApplicationListener<ServletWebServerInitializedEvent>{
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(ServerUri.class);

	@Value("${serverUri.host:localhost}")
	private String host;
	@Value("${serverUri.schema:http}")
	private String schema;
	@Value("${serverUri.port:0}")
	private int port;
	
	@Override
	public void onApplicationEvent(ServletWebServerInitializedEvent event) {
		if(port == 0) {
			port = event.getWebServer().getPort();
		}
	}
	
	public URI getUri() {
		try {
			return new URI(schema, null, host, port, null, null, null);
		} catch (URISyntaxException e) {
			LOGGER.info("Error in getting Uri", e.getMessage() );
			return null;
		}
	}

}
