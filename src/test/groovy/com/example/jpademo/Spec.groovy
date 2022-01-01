package com.example.jpademo

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgresInitalizer.class)
@AutoConfigureMockMvc
class Spec extends Specification {

    private static PostgreSQLContainer<?> postgreDBContainer = new PostgreSQLContainer<>("postgres:11.4")
        .withDatabaseName("test-db")

    static {
        postgreDBContainer.start()
    }

    public static class PostgresInitalizer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=" + postgreDBContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreDBContainer.getUsername(),
                "spring.datasource.password=" + postgreDBContainer.getPassword()
            );
        }
    }

}
