# 1-bosqich: Loyihani build qilish (JDK o'rnatilgan obraz)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2-bosqich: Loyihani ishga tushirish (Faqat JRE - yengil obraz)
FROM openjdk:17-jdk-slim
# Build qilingan .jar faylni nusxalaymiz
COPY --from=build /target/*.jar app.jar

# Render portini ochamiz (odatda 8080)
EXPOSE 8080

# Dasturni ishga tushirish
ENTRYPOINT ["java", "-jar", "app.jar"]