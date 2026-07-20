package com.arrupeteca.persistence.repository;

import com.arrupeteca.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.arrupeteca.config.JpaConfig;

/**
 * Base class for repository integration tests using H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import({JpaConfig.class, TestContainersConfig.class}) Para pruebas con docker
@Import(JpaConfig.class)
public abstract class BaseRepositoryTest {
}
