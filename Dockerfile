FROM maven:3.6-jdk-11 AS MAVEN_TOOL
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn clean compile assembly:single

FROM ubuntu:18.04
COPY --from=MAVEN_TOOL /tmp/target/ /wiibalance
RUN apt-get update && apt-get install --no-install-recommends -y default-jdk xorg libgl1-mesa-glx bluez libbluetooth-dev && rm -rf /var/lib/apt/lists/* && apt-get update
WORKDIR /wiibalance
RUN java -version
ENTRYPOINT ["java", "-jar", "Wiibalance-jar-with-dependencies.jar"]