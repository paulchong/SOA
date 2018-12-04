FROM alpine:3.8
MAINTAINER Paul Fremantle (paul@fremantle.org)

RUN apk --update add openjdk8-jre
	
RUN mkdir -p /home/root/purchase
ADD build/libs/purchase-0.0.2.jar /home/root/purchase
EXPOSE 8080
ENV REDIS_HOST "redis"
ENTRYPOINT java -jar /home/root/purchase/purchase-0.0.2.jar
