FROM maven:3.6.3 AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app
# RUN mvn package 

# FROM adoptopenjdk/openjdk11:alpine-jre

# ARG JAR_FILE=XMchallenge-0.0.1-SNAPSHOT.jar

# WORKDIR /opt/app
# COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

# ENTRYPOINT ["java","-jar","XMchallenge-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT mvn spring-boot:run