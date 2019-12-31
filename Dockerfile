FROM openjdk:8-jre-alpine
LABEL Paul Chong (paulchong.nz@gmail.com)
RUN mkdir -p /home/root/reservation
ADD build/libs/reservation-0.0.2.jar /home/root/reservation/
ENV REDIS_HOST "redis"
EXPOSE 8080
ENTRYPOINT java -jar /home/root/reservation/reservation-0.0.2.jar
