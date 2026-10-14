package com.jcaa.usersmanagement.infrastructure.adapter.account;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("AccountHttpClientAdapter")
class AccountHttpClientAdapterTest {

  @Test
  @DisplayName("createAccountForUser maneja el intento de conexion sin fallar abruptamente si accounts no esta disponible")
  void shouldHandleConnectionGracefullyWhenAccountsIsUnavailable() {
    // Arrange
    final AccountHttpClientAdapter adapter =
        new AccountHttpClientAdapter("http://localhost:59999");

    final UserModel user = new UserModel(
        new UserId("usr-1"),
        new UserName("Bob"),
        new UserEmail("bob@example.com"),
        UserPassword.fromPlainText("Pass123!"),
        UserRole.MEMBER,
        UserStatus.ACTIVE
    );

    // Act & Assert
    assertDoesNotThrow(() -> adapter.createAccountForUser(user));
  }
}
