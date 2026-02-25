package com.radarchart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 用于检查后端服务和依赖组件状态
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private static final double MEMORY_WARNING_THRESHOLD = 90.0;

    private final DataSource dataSource;

    public TestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 健康检查接口 - 检查后端服务状态
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "radar-chart-builder",
            "timestamp", LocalDateTime.now(),
            "message", "后端服务运行正常"
        );
    }

    /**
     * 依赖服务状态检查接口
     */
    @GetMapping("/test/services")
    public Map<String, Object> testServices() {
        Map<String, Object> services = new HashMap<>();
        services.put("database", checkDatabase());
        services.put("jvm", checkJvm());

        return Map.of(
            "status", calculateOverallStatus(services),
            "timestamp", LocalDateTime.now(),
            "services", services
        );
    }

    private Map<String, Object> checkDatabase() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            long startTime = System.nanoTime();
            stmt.execute("SELECT 1");
            long latency = (System.nanoTime() - startTime) / 1000;

            return Map.of(
                "status", "UP",
                "message", "数据库连接正常",
                "data", Map.of(
                    "latencyMicros", latency,
                    "latencyMs", String.format("%.2f", latency / 1000.0),
                    "url", conn.getMetaData().getURL(),
                    "database", conn.getMetaData().getDatabaseProductName()
                )
            );
        } catch (Exception e) {
            return Map.of(
                "status", "DOWN",
                "message", "数据库连接失败",
                "error", e.getMessage()
            );
        }
    }

    private Map<String, Object> checkJvm() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double memoryUsage = (double) usedMemory / maxMemory * 100;

        return Map.of(
            "status", memoryUsage < MEMORY_WARNING_THRESHOLD ? "UP" : "DEGRADED",
            "message", String.format("内存使用率 %.1f%%", memoryUsage),
            "data", Map.of(
                "maxMemoryMB", maxMemory / 1024 / 1024,
                "usedMemoryMB", usedMemory / 1024 / 1024,
                "memoryUsagePercent", String.format("%.1f%%", memoryUsage),
                "processors", runtime.availableProcessors(),
                "uptime", formatUptime(ManagementFactory.getRuntimeMXBean().getUptime())
            )
        );
    }

    private String calculateOverallStatus(Map<String, Object> services) {
        return services.values().stream()
            .map(m -> (Map<String, Object>) m)
            .allMatch(m -> "UP".equals(m.get("status"))) ? "UP" : "DEGRADED";
    }

    private String formatUptime(long uptimeMs) {
        Duration duration = Duration.ofMillis(uptimeMs);
        return String.format("%dd %dh %dm %ds",
            duration.toDays(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
    }

}
