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

package com.subhrodip.voltmasters.transaction.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhrodip.voltmasters.transaction.model.AuthorizationRequest;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;

public class AuthorizationRequestDeserializer implements Deserializer<AuthorizationRequest> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @Override
  public AuthorizationRequest deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(data, AuthorizationRequest.class);
    } catch (Exception e) {
      throw new RuntimeException("Error deserializing AuthorizationRequest", e);
    }
  }

  @Override
  public void close() {}
}
