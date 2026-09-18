package com.balansys.springbt_krb5_databases_demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {

        String version =
                jdbcTemplate.queryForObject(
                        "select version()",
                        String.class);

        System.out.println(version);
    }
}