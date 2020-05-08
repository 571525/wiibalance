FROM maven:3.6-jdk-11 AS MAVEN_TOOL
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM ubuntu:18.04
COPY --from=MAVEN_TOOL /tmp/target/ /wiibalance
RUN apt-get update && apt-get install --no-install-recommends -y xorg libgl1-mesa-glx && rm -rf /var/lib/apt/lists/*
WORKDIR /wiibalance
CMD java -jar wiibalance-1.0-SNAPSHOT.jar