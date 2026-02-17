package com.radarchart.controller;

import com.radarchart.dto.param.*;
import com.radarchart.dto.result.*;
import com.radarchart.service.RadarChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 雷达图管理控制器
 */
@Tag(name = "雷达图管理", description = "雷达图CRUD、数据系列管理")
@RestController
@RequestMapping("/api/radar-charts")
public class RadarChartController {

    private final RadarChartService radarChartService;

    public RadarChartController(RadarChartService radarChartService) {
        this.radarChartService = radarChartService;
    }

    @Operation(summary = "创建雷达图")
    @PostMapping
    public RadarChartResult createRadarChart(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateRadarChartParam param) {
        return radarChartService.createRadarChart(userId, param);
    }

    @Operation(summary = "获取当前用户的雷达图列表")
    @GetMapping
    public List<RadarChartResult> getUserRadarCharts(
            @AuthenticationPrincipal Long userId) {
        return radarChartService.getUserRadarCharts(userId);
    }

    @Operation(summary = "获取雷达图详情")
    @GetMapping("/{id}")
    public RadarChartResult getRadarChartDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return radarChartService.getRadarChartDetail(id, userId);
    }

    @Operation(summary = "更新雷达图基本信息")
    @PutMapping("/{id}")
    public RadarChartResult updateRadarChart(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateRadarChartParam param) {
        return radarChartService.updateRadarChart(id, userId, param);
    }

    @Operation(summary = "删除雷达图")
    @DeleteMapping("/{id}")
    public void deleteRadarChart(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        radarChartService.deleteRadarChart(id, userId);
    }

    @Operation(summary = "添加数据系列")
    @PostMapping("/{id}/series")
    public SeriesResult addDataSeries(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddDataSeriesParam param) {
        return radarChartService.addDataSeries(id, userId, param);
    }

    @Operation(summary = "获取所有数据系列")
    @GetMapping("/{id}/series")
    public List<SeriesResult> getDataSeries(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return radarChartService.getDataSeries(id, userId);
    }

    @Operation(summary = "更新系列名称")
    @PutMapping("/{id}/series/{seriesId}")
    public void updateSeriesName(
            @PathVariable Long id,
            @PathVariable Long seriesId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateSeriesNameParam param) {
        radarChartService.updateSeriesName(id, seriesId, userId, param);
    }

    @Operation(summary = "删除数据系列")
    @DeleteMapping("/{id}/series/{seriesId}")
    public void deleteSeries(
            @PathVariable Long id,
            @PathVariable Long seriesId,
            @AuthenticationPrincipal Long userId) {
        radarChartService.deleteSeries(id, seriesId, userId);
    }

    @Operation(summary = "更新系列数据值")
    @PutMapping("/{id}/series/{seriesId}/data")
    public void updateSeriesData(
            @PathVariable Long id,
            @PathVariable Long seriesId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateSeriesDataParam param) {
        radarChartService.updateSeriesData(id, seriesId, userId, param);
    }

    @Operation(summary = "获取图表数据（ECharts格式）")
    @GetMapping("/{id}/export")
    public ChartDataResult getChartData(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return radarChartService.getChartData(id, userId);
    }
}
