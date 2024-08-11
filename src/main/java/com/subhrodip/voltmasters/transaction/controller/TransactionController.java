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
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

  private ReplyingKafkaTemplate<UUID, ChargingRequest, AuthorizationResponse> replyingKafkaTemplate;

  @Autowired
  public TransactionController(
      ReplyingKafkaTemplate<UUID, ChargingRequest, AuthorizationResponse> replyingKafkaTemplate) {
    this.replyingKafkaTemplate = replyingKafkaTemplate;
  }

  @PostMapping("/authorize")
  @CrossOrigin
  public AuthorizationResponse authorize(@RequestBody @Valid AuthorizationRequest request)
      throws InterruptedException, ExecutionException, TimeoutException {

    UUID key = UUID.randomUUID();

    RequestReplyFuture<UUID, ChargingRequest, AuthorizationResponse> requestReplyFuture =
        replyingKafkaTemplate.sendAndReceive(
            new ProducerRecord<>(
                KafkaTopics.CHARGE_AUTHORIZATION_INPUT_TOPIC,
                key,
                new ChargingRequest(key, request)));

    return requestReplyFuture.get(25, TimeUnit.SECONDS).value();
  }
}
