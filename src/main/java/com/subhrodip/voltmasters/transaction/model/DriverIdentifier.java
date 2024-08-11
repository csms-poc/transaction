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

package com.subhrodip.voltmasters.transaction.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DriverIdentifier(
    @NotBlank(message = "Driver ID cannot be blank")
        @Size(min = 20, max = 80, message = "Driver ID must be between 20 and 80 characters")
        String id) {}
