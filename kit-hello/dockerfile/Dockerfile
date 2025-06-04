FROM openjdk:11-jre-slim
VOLUME /tmp
WORKDIR /app
COPY target/ ./app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
