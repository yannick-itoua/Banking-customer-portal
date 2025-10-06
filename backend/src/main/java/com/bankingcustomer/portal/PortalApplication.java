package com.bankingcustomer.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;

@SpringBootApplication(exclude = {SystemMetricsAutoConfiguration.class})
public class PortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortalApplication.class, args);
	}

}
