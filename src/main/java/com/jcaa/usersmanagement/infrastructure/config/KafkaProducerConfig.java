package com.jcaa.usersmanagement.infrastructure.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
  private String bootstrapServers;

  @Value("${spring.kafka.properties.security.protocol:PLAINTEXT}")
  private String securityProtocol;

  @Value("${spring.kafka.properties.ssl.truststore.location:}")
  private String sslTruststoreLocation;

  @Value("${spring.kafka.properties.ssl.truststore.password:}")
  private String sslTruststorePassword;

  @Value("${spring.kafka.properties.ssl.keystore.location:}")
  private String sslKeystoreLocation;

  @Value("${spring.kafka.properties.ssl.keystore.password:}")
  private String sslKeystorePassword;

  @Value("${spring.kafka.properties.ssl.key.password:}")
  private String sslKeyPassword;

  @Value("${spring.kafka.properties.sasl.mechanism:}")
  private String saslMechanism;

  @Value("${spring.kafka.properties.sasl.jaas.config:}")
  private String saslJaasConfig;

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    final Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    if (securityProtocol != null && !securityProtocol.isBlank() && !"PLAINTEXT".equalsIgnoreCase(securityProtocol)) {
      configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
    }
    if (sslTruststoreLocation != null && !sslTruststoreLocation.isBlank()) {
      configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocation);
    }
    if (sslTruststorePassword != null && !sslTruststorePassword.isBlank()) {
      configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePassword);
    }
    if (sslKeystoreLocation != null && !sslKeystoreLocation.isBlank()) {
      configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslKeystoreLocation);
    }
    if (sslKeystorePassword != null && !sslKeystorePassword.isBlank()) {
      configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslKeystorePassword);
    }
    if (sslKeyPassword != null && !sslKeyPassword.isBlank()) {
      configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, sslKeyPassword);
    }
    if (saslMechanism != null && !saslMechanism.isBlank()) {
      configProps.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
    }
    if (saslJaasConfig != null && !saslJaasConfig.isBlank()) {
      configProps.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
    }

    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(final ProducerFactory<String, String> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }
}
