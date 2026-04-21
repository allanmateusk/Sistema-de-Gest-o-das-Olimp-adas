FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle --no-daemon clean build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt
COPY --from=build /app/build/distributions/sgo-backend-0.1.0.tar /tmp/app.tar
RUN tar -xf /tmp/app.tar -C /opt && rm /tmp/app.tar
WORKDIR /opt/sgo-backend-0.1.0
EXPOSE 8080
ENTRYPOINT ["./bin/sgo-backend"]
