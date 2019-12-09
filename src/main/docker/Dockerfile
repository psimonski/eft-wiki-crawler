FROM openjdk:8-jdk-alpine
MAINTAINER Simon Pfeifer
VOLUME /tmp
ADD eft-wiki-crawler.jar eft-wiki-crawler.jar
RUN echo "Europe/Berlin" > /etc/timezone
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/eft-wiki-crawler.jar"]

# -- DEBUGGING --
# EXPOSE 5005
# ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -Djava.security.egd=file:/dev/./urandom","-jar","/ptrader-collector.jar"]