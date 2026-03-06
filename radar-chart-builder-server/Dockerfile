# 构建阶段
FROM maven:3.9.12-eclipse-temurin-21 AS builder

WORKDIR /radar-chart-builder-server

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre

WORKDIR /radar-chart-builder-server

# 设置时区为亚洲/上海
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 复制构建产物
COPY --from=builder /radar-chart-builder-server/target/application.jar application.jar

# 非 root 用户运行
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /radar-chart-builder-server
USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "application.jar", "--spring.profiles.active=prod"]
