#
# transaction - Transaction Service for Receiving Requests from Charging Stations
# Copyright © 2024 Subhrodip Mohanta (contact@subhrodip.com)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

FROM maven:3-amazoncorretto-21-al2023 as build

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine

LABEL maintainer="Subhrodip Mohanta <contact@subhrodip.com>"
LABEL group="com.subhrodip.voltmasters"
LABEL artifact="transaction"
LABEL platform="java"
LABEL name="Transaction Server"
LABEL org.opencontainers.image.source="https://github.com/voltmasters/transaction"

ARG JAR_FILE=target/*.jar

COPY --from=build /app/${JAR_FILE} app.jar

# If you are changing server port, be sure to change this as well
EXPOSE 8080

#Running the application with `prod` profile
ENTRYPOINT [ "java", "-jar", "-Dspring.profiles.active=prod", "/app.jar" ]
