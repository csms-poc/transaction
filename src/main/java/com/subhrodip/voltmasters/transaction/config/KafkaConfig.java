/*
 * transaction - Transaction Service for Receiving Requests from Charging Stations
 * Copyright © 2024 Subhrodip Mohanta (contact@subhrodip.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.subhrodip.voltmasters.transaction.config;

import com.subhrodip.voltmasters.transaction.model.AuthorizationResponse;
import com.subhrodip.voltmasters.transaction.model.ChargingRequest;
import com.subhrodip.voltmasters.transaction.serdes.AuthorizationResponseDeserializer;
import com.subhrodip.voltmasters.transaction.serdes.ChargingRequestSerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

@Configuration
@EnableKafka
public class KafkaConfig {

  @Value(value = "${spring.kafka.bootstrap-servers:localhost:9092}")
  private String bootstrapAddress;

  @Bean
  public ProducerFactory<UUID, ChargingRequest> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ChargingRequestSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<UUID, ChargingRequest> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ConsumerFactory<UUID, AuthorizationResponse> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "csms-transaction-auth-service");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AuthorizationResponseDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  NewTopic inputTopic() {
    return TopicBuilder.name(KafkaTopics.CHARGE_AUTHORIZATION_INPUT_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  NewTopic outputTopic() {
    return TopicBuilder.name(KafkaTopics.CHARGE_AUTHORIZATION_OUTPUT_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }
}
