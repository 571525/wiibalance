FROM maven:3.6-jdk-11 AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM ubuntu:18.04
RUN mkdir wiibalance
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/wiibalance*.jar /wiibalance
RUN apt-get update && apt-get install --no-install-recommends -y openjfx && rm -rf /var/lib/apt/lists/*
WORKDIR /wiibalance
CMD java -jar target/wiibalance-1.0-SNAPSHOT.jar