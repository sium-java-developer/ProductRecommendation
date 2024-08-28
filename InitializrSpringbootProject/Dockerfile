

FROM openjdk:17-slim

WORKDIR /productRecommendation

COPY pom.xml ./
RUN mvn clean package

COPY target/*.jar ./

EXPOSE 8080

CMD ["java", "-jar", "productRecommendation.jar"]
