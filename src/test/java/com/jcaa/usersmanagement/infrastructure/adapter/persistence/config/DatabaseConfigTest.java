package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

  private static final String HOST = "postgres.example.com";
  private static final int PORT = 5432;
  private static final String DATABASE = "crud_usuarios";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "secret";
  private static final String SSL_MODE = "require";

  @Test
  void shouldBuildJdbcUrlWithConfiguredSslMode() {
    // Arrange
    final DatabaseConfig config =
        new DatabaseConfig(HOST, PORT, DATABASE, USERNAME, PASSWORD, SSL_MODE);

    // Act
    final String jdbcUrl = config.buildJdbcUrl();

    // Assert
    assertThat(jdbcUrl)
        .isEqualTo("jdbc:postgresql://postgres.example.com:5432/crud_usuarios?sslmode=require");
  }
}
