package com.casandra.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
@PropertySource(value = {
		"application.properties", "config.properties" })
public class CassandraApp extends SpringBootServletInitializer implements WebApplicationInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CassandraApp.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CassandraApp.class, args);
	}

}
