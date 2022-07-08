FROM maven:3.6.3 AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn clean package -DskipTests 

ENTRYPOINT mvn spring-boot:run