package com.btg.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.btg.core")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TestApplication {
}
