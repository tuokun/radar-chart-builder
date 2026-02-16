package com.radarchart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.radarchart.dto.param.*;
import com.radarchart.dto.result.*;
import com.radarchart.entity.*;
import com.radarchart.exception.BadRequestException;
import com.radarchart.exception.ResourceNotFoundException;
import com.radarchart.mapper.*;
import com.radarchart.service.RadarChartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RadarChartServiceImpl implements RadarChartService {

    private final RadarChartMapper radarChartMapper;
    private final DimensionMapper dimensionMapper;
    private final DataSeriesMapper dataSeriesMapper;
    private final SeriesDataMapper seriesDataMapper;

    public RadarChartServiceImpl(RadarChartMapper radarChartMapper,
                                  DimensionMapper dimensionMapper,
                                  DataSeriesMapper dataSeriesMapper,
                                  SeriesDataMapper seriesDataMapper) {
        this.radarChartMapper = radarChartMapper;
        this.dimensionMapper = dimensionMapper;
        this.dataSeriesMapper = dataSeriesMapper;
        this.seriesDataMapper = seriesDataMapper;
    }

    @Override
    @Transactional
    public RadarChartResult createRadarChart(Long userId, CreateRadarChartParam param) {
        // TODO: Task 7 实现
        return null;
    }

    @Override
    public List<RadarChartResult> getUserRadarCharts(Long userId) {
        // TODO: Task 8 实现
        return null;
    }

    @Override
    public RadarChartResult getRadarChartDetail(Long id, Long userId) {
        // TODO: Task 9 实现
        return null;
    }

    @Override
    @Transactional
    public RadarChartResult updateRadarChart(Long id, Long userId, UpdateRadarChartParam param) {
        // TODO: Task 10 实现
        return null;
    }

    @Override
    @Transactional
    public void deleteRadarChart(Long id, Long userId) {
        // TODO: Task 11 实现
    }

    @Override
    @Transactional
    public SeriesResult addDataSeries(Long radarChartId, Long userId, AddDataSeriesParam param) {
        // TODO: Task 12 实现
        return null;
    }

    @Override
    public List<SeriesResult> getDataSeries(Long radarChartId, Long userId) {
        // TODO: Task 13 实现
        return null;
    }

    @Override
    @Transactional
    public void updateSeriesName(Long radarChartId, Long seriesId, Long userId, UpdateSeriesNameParam param) {
        // TODO: Task 14 实现
    }

    @Override
    @Transactional
    public void deleteSeries(Long radarChartId, Long seriesId, Long userId) {
        // TODO: Task 15 实现
    }

    @Override
    @Transactional
    public void updateSeriesData(Long radarChartId, Long seriesId, Long userId, UpdateSeriesDataParam param) {
        // TODO: Task 16 实现
    }

    @Override
    public ChartDataResult getChartData(Long id, Long userId) {
        // TODO: Task 17 实现
        return null;
    }

    // 辅助方法
    private RadarChart getAndValidateRadarChart(Long id, Long userId) {
        RadarChart chart = radarChartMapper.selectById(id);
        if (chart == null) {
            throw new ResourceNotFoundException("雷达图不存在");
        }
        if (!chart.getUserId().equals(userId)) {
            throw new BadRequestException("无权操作此雷达图");
        }
        return chart;
    }
}
