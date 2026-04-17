FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY src ./src
RUN javac src/server/*.java
EXPOSE 12345
CMD ["java", "-cp", "src", "server.Server"]