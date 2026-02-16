package com.radarchart.service;

import com.radarchart.dto.param.*;
import com.radarchart.dto.result.*;

import java.util.List;

/**
 * 雷达图核心功能服务接口
 */
public interface RadarChartService {

    // ==================== 雷达图CRUD ====================

    /**
     * 创建雷达图（含维度配置）
     *
     * @param userId 用户ID
     * @param param 创建参数（标题、描述、维度列表）
     * @return 创建的雷达图详情
     */
    RadarChartResult createRadarChart(Long userId, CreateRadarChartParam param);

    /**
     * 获取当前用户的雷达图列表
     *
     * @param userId 用户ID
     * @return 雷达图列表
     */
    List<RadarChartResult> getUserRadarCharts(Long userId);

    /**
     * 获取雷达图详情（含维度、系列、数据）
     *
     * @param id 雷达图ID
     * @param userId 用户ID（用于权限验证）
     * @return 雷达图详情
     */
    RadarChartResult getRadarChartDetail(Long id, Long userId);

    /**
     * 更新雷达图基本信息（标题、描述）
     *
     * @param id 雷达图ID
     * @param userId 用户ID（用于权限验证）
     * @param param 更新参数
     * @return 更新后的雷达图详情
     */
    RadarChartResult updateRadarChart(Long id, Long userId, UpdateRadarChartParam param);

    /**
     * 删除雷达图（级联删除所有关联数据）
     *
     * @param id 雷达图ID
     * @param userId 用户ID（用于权限验证）
     */
    void deleteRadarChart(Long id, Long userId);

    // ==================== 数据系列管理 ====================

    /**
     * 添加数据系列
     *
     * @param radarChartId 雷达图ID
     * @param userId 用户ID（用于权限验证）
     * @param param 系列参数（系列名称、数据值）
     * @return 创建的数据系列详情
     */
    SeriesResult addDataSeries(Long radarChartId, Long userId, AddDataSeriesParam param);

    /**
     * 获取雷达图的所有数据系列
     *
     * @param radarChartId 雷达图ID
     * @param userId 用户ID（用于权限验证）
     * @return 数据系列列表
     */
    List<SeriesResult> getDataSeries(Long radarChartId, Long userId);

    /**
     * 更新数据系列名称
     *
     * @param radarChartId 雷达图ID
     * @param seriesId 系列ID
     * @param userId 用户ID（用于权限验证）
     * @param param 更新参数（新名称）
     */
    void updateSeriesName(Long radarChartId, Long seriesId, Long userId, UpdateSeriesNameParam param);

    /**
     * 删除数据系列（级联删除该系列的所有数据点）
     *
     * @param radarChartId 雷达图ID
     * @param seriesId 系列ID
     * @param userId 用户ID（用于权限验证）
     */
    void deleteSeries(Long radarChartId, Long seriesId, Long userId);

    /**
     * 更新数据系列的数据值
     *
     * @param radarChartId 雷达图ID
     * @param seriesId 系列ID
     * @param userId 用户ID（用于权限验证）
     * @param param 更新参数（新的数据值）
     */
    void updateSeriesData(Long radarChartId, Long seriesId, Long userId, UpdateSeriesDataParam param);

    // ==================== 导出 ====================

    /**
     * 获取图表数据（ECharts格式）
     *
     * @param id 雷达图ID
     * @param userId 用户ID（用于权限验证）
     * @return ECharts格式的图表数据
     */
    ChartDataResult getChartData(Long id, Long userId);
}
