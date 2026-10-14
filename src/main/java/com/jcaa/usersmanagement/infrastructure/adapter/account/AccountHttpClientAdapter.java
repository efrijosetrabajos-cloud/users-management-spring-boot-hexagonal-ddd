package com.jcaa.usersmanagement.infrastructure.adapter.account;

import com.jcaa.usersmanagement.application.port.out.AccountClientPort;
import com.jcaa.usersmanagement.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class AccountHttpClientAdapter implements AccountClientPort {

  private final RestClient restClient;
  private final String accountsUrl;

  public AccountHttpClientAdapter(
      @Value("${accounts.service.url:http://localhost:8081}") final String accountsUrl) {
    this.accountsUrl = accountsUrl;
    this.restClient = RestClient.builder().baseUrl(accountsUrl).build();
  }

  @Override
  public void createAccountForUser(final User user) {
    try {
      log.info(
          "[AccountHttpClientAdapter] Solicitando creacion de cuenta para usuario id={} en {}",
          user.getId().value(),
          accountsUrl);

      final Map<String, Object> request = new LinkedHashMap<>();
      request.put("userId", user.getId().value());
      request.put("email", user.getEmail().value());
      request.put("name", user.getName().value());
      request.put("role", user.getRole().name());

      restClient
          .post()
          .uri("/api/accounts")
          .contentType(MediaType.APPLICATION_JSON)
          .body(request)
          .retrieve()
          .toBodilessEntity();

      log.info(
          "[AccountHttpClientAdapter] Cuenta solicitada exitosamente para usuario id={}",
          user.getId().value());
    } catch (Exception e) {
      log.warn(
          "[AccountHttpClientAdapter] No se pudo comunicar con el servicio accounts en {}: {}",
          accountsUrl,
          e.getMessage());
    }
  }
}
