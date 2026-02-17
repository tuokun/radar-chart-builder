-- ============================================
-- 雷达图生成器项目 - 数据库初始化脚本
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS radar_chart_builder
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE radar_chart_builder;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
  nickname VARCHAR(50) COMMENT '昵称',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  INDEX idx_username (username),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 雷达图相关表
-- ============================================

-- 雷达图表
CREATE TABLE IF NOT EXISTS radar_chart (
    id BIGINT PRIMARY KEY COMMENT '雷达图ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='雷达图表';

-- 维度表
CREATE TABLE IF NOT EXISTS dimension (
    id BIGINT PRIMARY KEY COMMENT '维度ID',
    radar_chart_id BIGINT NOT NULL COMMENT '所属雷达图ID',
    name VARCHAR(50) NOT NULL COMMENT '维度名称',
    min_value DOUBLE NOT NULL DEFAULT 0 COMMENT '最小值',
    max_value DOUBLE NOT NULL DEFAULT 100 COMMENT '最大值',
    order_index INT NOT NULL COMMENT '排序索引',
    INDEX idx_radar_chart_id (radar_chart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维度表';

-- 数据系列表
CREATE TABLE IF NOT EXISTS data_series (
    id BIGINT PRIMARY KEY COMMENT '系列ID',
    radar_chart_id BIGINT NOT NULL COMMENT '所属雷达图ID',
    name VARCHAR(50) NOT NULL COMMENT '系列名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_radar_chart_id (radar_chart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据系列表';

-- 系列数据表
CREATE TABLE IF NOT EXISTS series_data (
    id BIGINT PRIMARY KEY COMMENT '数据ID',
    series_id BIGINT NOT NULL COMMENT '所属系列ID',
    dimension_id BIGINT NOT NULL COMMENT '所属维度ID',
    value DOUBLE NOT NULL COMMENT '数值',
    INDEX idx_series_id (series_id),
    INDEX idx_dimension_id (dimension_id),
    UNIQUE KEY uk_series_dimension (series_id, dimension_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系列数据表';

-- ============================================
-- 初始化完成
-- ============================================
-- 数据库结构创建完成，包含以下表：
-- 1. users - 用户表
-- 2. radar_chart - 雷达图表
-- 3. dimension - 维度表
-- 4. data_series - 数据系列表
-- 5. series_data - 系列数据表
