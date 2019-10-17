FROM java:8-jdk-alpine
COPY 'target/voicerecorder-1.0-SNAPSHOT.jar' /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENV JAVA_OPTS="-Xmx256m -Xms256m"
ENTRYPOINT ["java", "-jar", "voicerecorder-1.0-SNAPSHOT.jar"]