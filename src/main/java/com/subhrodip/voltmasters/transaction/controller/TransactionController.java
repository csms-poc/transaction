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

package com.subhrodip.voltmasters.transaction.controller;

import com.subhrodip.voltmasters.transaction.config.KafkaTopics;
import com.subhrodip.voltmasters.transaction.model.AuthorizationRequest;
import com.subhrodip.voltmasters.transaction.model.AuthorizationResponse;
import com.subhrodip.voltmasters.transaction.model.ChargingRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.concurrent.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

  private final KafkaTemplate<UUID, ChargingRequest> kafkaTemplate;
  private final ConcurrentMap<UUID, CompletableFuture<AuthorizationResponse>> responseFutures =
      new ConcurrentHashMap<>();

  @Autowired
  public TransactionController(KafkaTemplate<UUID, ChargingRequest> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @PostMapping("/authorize")
  @CrossOrigin
  public AuthorizationResponse authorize(@RequestBody @Valid AuthorizationRequest request)
      throws InterruptedException, ExecutionException, TimeoutException {
    UUID key = UUID.randomUUID();
    CompletableFuture<AuthorizationResponse> future = new CompletableFuture<>();
    responseFutures.put(key, future);

    kafkaTemplate.send(
        KafkaTopics.CHARGE_AUTHORIZATION_INPUT_TOPIC, key, new ChargingRequest(key, request));

    return future.get(30, TimeUnit.SECONDS);
  }

  @KafkaListener(
      topics = KafkaTopics.CHARGE_AUTHORIZATION_OUTPUT_TOPIC,
      groupId = "csms-transaction-auth-service")
  public void listen(ConsumerRecord<UUID, AuthorizationResponse> record) {
    UUID key = record.key();
    AuthorizationResponse response = record.value();
    CompletableFuture<AuthorizationResponse> future = responseFutures.remove(key);
    if (future != null) {
      future.complete(response);
    }
  }
}
