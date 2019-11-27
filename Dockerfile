FROM openjdk:8-jre-alpine
MAINTAINER Paul Fremantle (paul@fremantle.org)
RUN mkdir -p /home/root/purchase
ADD build/libs/purchase-0.0.2.jar /home/root/purchase/
ENV REDIS_HOST "redis"
EXPOSE 8080
ENTRYPOINT java -jar /home/root/purchase/purchase-0.0.2.jar
