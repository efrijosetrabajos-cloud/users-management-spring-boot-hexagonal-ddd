package com.jcaa.usersmanagement.infrastructure.adapter.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcaa.usersmanagement.domain.model.User;
import com.jcaa.usersmanagement.domain.port.EventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String topic;

  @Autowired
  public KafkaEventPublisherAdapter(
      final KafkaTemplate<String, String> kafkaTemplate,
      final ObjectMapper objectMapper,
      @Value("${kafka.topic.user-events:user-events}") final String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    this.topic = topic;
  }

  @Override
  public void publishUserCreated(final User user) {
    try {
      final Map<String, Object> payload = new LinkedHashMap<>();
      payload.put("id", user.getId().value());
      payload.put("name", user.getName().value());
      payload.put("email", user.getEmail().value());
      payload.put("role", user.getRole().name());
      payload.put("status", user.getStatus().name());

      final String json = objectMapper.writeValueAsString(payload);
      final String key = user.getId().value();

      kafkaTemplate.send(topic, key, json);
      log.info("[KafkaEventPublisherAdapter] Evento publicado en topico '{}' para usuario id={}", topic, key);
    } catch (JsonProcessingException e) {
      log.error("[KafkaEventPublisherAdapter] Error al serializar el usuario a JSON para Kafka", e);
      throw new RuntimeException("Error al serializar el evento de usuario a JSON", e);
    }
  }
}
