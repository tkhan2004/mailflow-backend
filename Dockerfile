# Sử dụng image Java 17 chính thức
FROM eclipse-temurin:17-jdk

# Tạo thư mục chứa app trong container
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Build ứng dụng (dùng Maven Wrapper nếu có)
RUN ./mvnw clean package -DskipTests

# Chạy ứng dụng bằng file JAR đã build
CMD ["java", "-jar", "target/mailflow-backend-0.0.1-SNAPSHOT.jar"]