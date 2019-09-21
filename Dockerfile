FROM java:8-jdk-alpine
COPY out/artifacts/VoiceRecorder_jar/VoiceRecorder.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "VoiceRecorder.jar"]