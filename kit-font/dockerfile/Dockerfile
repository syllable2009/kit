FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/kit-font.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar","--spring.profiles.active=prod","-c"]
