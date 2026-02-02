# 1-bosqich: Build (Maven yordamida loyihani yig'ish)
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2-bosqich: Run (Yig'ilgan dasturni ishga tushirish)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Build bosqichidan .jar faylni nusxalaymiz
# Eslatma: Agar loyihangiz nomi boshqacha bo'lsa, 'target/*.jar' o'zi topadi
COPY --from=build /app/target/*.jar app.jar

# Render uchun portni ochamiz
EXPOSE 8080

# Dasturni ishga tushirish buyrug'i
ENTRYPOINT ["java", "-jar", "app.jar"]
