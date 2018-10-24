package com.chamc.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude={JpaRepositoriesAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class RedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);
	}
}
