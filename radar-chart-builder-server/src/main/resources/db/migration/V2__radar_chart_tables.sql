-- 雷达图表
CREATE TABLE IF NOT EXISTS radar_chart (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='雷达图表';

-- 维度表
CREATE TABLE IF NOT EXISTS dimension (
    id BIGINT PRIMARY KEY,
    radar_chart_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    min_value DOUBLE NOT NULL DEFAULT 0,
    max_value DOUBLE NOT NULL DEFAULT 100,
    order_index INT NOT NULL,
    INDEX idx_radar_chart_id (radar_chart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维度表';

-- 数据系列表
CREATE TABLE IF NOT EXISTS data_series (
    id BIGINT PRIMARY KEY,
    radar_chart_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_radar_chart_id (radar_chart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据系列表';

-- 系列数据表
CREATE TABLE IF NOT EXISTS series_data (
    id BIGINT PRIMARY KEY,
    series_id BIGINT NOT NULL,
    dimension_id BIGINT NOT NULL,
    value DOUBLE NOT NULL,
    INDEX idx_series_id (series_id),
    INDEX idx_dimension_id (dimension_id),
    UNIQUE KEY uk_series_dimension (series_id, dimension_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系列数据表';
