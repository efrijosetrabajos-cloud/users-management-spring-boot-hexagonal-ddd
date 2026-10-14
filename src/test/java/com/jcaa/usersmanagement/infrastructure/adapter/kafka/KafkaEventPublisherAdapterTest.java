package com.jcaa.usersmanagement.infrastructure.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DisplayName("KafkaEventPublisherAdapter")
@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherAdapterTest {

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final String topic = "user-events";

  private KafkaEventPublisherAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new KafkaEventPublisherAdapter(kafkaTemplate, objectMapper, topic);
  }

  @Test
  @DisplayName("publishUserCreated serializa y publica el evento en el topico user-events")
  void shouldPublishUserCreatedEvent() {
    // Arrange
    final UserModel user = new UserModel(
        new UserId("usr-123"),
        new UserName("Alice Smith"),
        new UserEmail("alice@example.com"),
        UserPassword.fromPlainText("Secret123"),
        UserRole.MEMBER,
        UserStatus.ACTIVE
    );

    // Act
    adapter.publishUserCreated(user);

    // Assert
    final ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
    verify(kafkaTemplate).send(eq("user-events"), eq("usr-123"), payloadCaptor.capture());

    final String jsonPayload = payloadCaptor.getValue();
    assertThat(jsonPayload).contains("\"id\":\"usr-123\"");
    assertThat(jsonPayload).contains("\"name\":\"Alice Smith\"");
    assertThat(jsonPayload).contains("\"email\":\"alice@example.com\"");
    assertThat(jsonPayload).contains("\"role\":\"MEMBER\"");
    assertThat(jsonPayload).contains("\"status\":\"ACTIVE\"");
  }
}
