FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/api-gateway-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx512m --enable-preview"
ENV TZ=Asia/Shanghai

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 