#This was part of an attempt to dockerize the appication
#It has been left in case someone would want to continue with this project and needs a dockerized application
#we found limitations in terms of bluetooth compatibility with the bluecove library and didn't have the resources
#to find a proper solution.

FROM maven:3.6-jdk-11 AS MAVEN_TOOL
COPY pom.xml /tmp/
COPY libs /tmp/libs/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM ubuntu:18.04
COPY --from=MAVEN_TOOL /tmp/target/ /wiibalance
RUN apt-get update && apt-get install --no-install-recommends -y xorg default-jdk libgl1-mesa-glx libbluetooth-dev bluetooth bluez bluez-tools && rm -rf /var/lib/apt/lists/*
RUN apt-get update && service bluetooth start
WORKDIR /wiibalance
CMD java -jar Wiibalance-jar-with-dependencies.jar