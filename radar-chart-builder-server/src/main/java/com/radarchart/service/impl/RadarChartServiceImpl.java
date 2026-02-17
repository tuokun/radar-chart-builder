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
        // 1. 创建雷达图
        RadarChart chart = new RadarChart();
        chart.setUserId(userId);
        chart.setTitle(param.getTitle());
        chart.setDescription(param.getDescription());
        radarChartMapper.insert(chart);

        // 2. 批量创建维度
        List<Dimension> dimensions = new ArrayList<>();
        for (CreateRadarChartParam.DimensionParam dimParam : param.getDimensions()) {
            Dimension dim = new Dimension();
            dim.setRadarChartId(chart.getId());
            dim.setName(dimParam.getName());
            dim.setMinValue(dimParam.getMinValue());
            dim.setMaxValue(dimParam.getMaxValue());
            dim.setOrderIndex(dimParam.getOrderIndex());
            dimensions.add(dim);
            dimensionMapper.insert(dim);
        }

        // 3. 构建返回结果
        return buildRadarChartResult(chart, dimensions, Collections.emptyList());
    }

    @Override
    public List<RadarChartResult> getUserRadarCharts(Long userId) {
        // 查询用户的所有雷达图
        List<RadarChart> charts = radarChartMapper.selectList(
                new LambdaQueryWrapper<RadarChart>()
                        .eq(RadarChart::getUserId, userId)
                        .orderByDesc(RadarChart::getCreateTime)
        );

        // 为每个图表加载维度和系列
        List<RadarChartResult> results = new ArrayList<>();
        for (RadarChart chart : charts) {
            List<Dimension> dimensions = dimensionMapper.selectList(
                    new LambdaQueryWrapper<Dimension>()
                            .eq(Dimension::getRadarChartId, chart.getId())
            );
            List<DataSeries> seriesList = dataSeriesMapper.selectList(
                    new LambdaQueryWrapper<DataSeries>()
                            .eq(DataSeries::getRadarChartId, chart.getId())
            );
            results.add(buildRadarChartResult(chart, dimensions, seriesList));
        }
        return results;
    }

    @Override
    public RadarChartResult getRadarChartDetail(Long id, Long userId) {
        RadarChart chart = getAndValidateRadarChart(id, userId);

        List<Dimension> dimensions = dimensionMapper.selectList(
                new LambdaQueryWrapper<Dimension>()
                        .eq(Dimension::getRadarChartId, id)
        );

        List<DataSeries> seriesList = dataSeriesMapper.selectList(
                new LambdaQueryWrapper<DataSeries>()
                        .eq(DataSeries::getRadarChartId, id)
        );

        return buildRadarChartResult(chart, dimensions, seriesList);
    }

    @Override
    @Transactional
    public RadarChartResult updateRadarChart(Long id, Long userId, UpdateRadarChartParam param) {
        RadarChart chart = getAndValidateRadarChart(id, userId);

        if (param.getTitle() != null) {
            chart.setTitle(param.getTitle());
        }
        if (param.getDescription() != null) {
            chart.setDescription(param.getDescription());
        }

        radarChartMapper.updateById(chart);

        return getRadarChartDetail(id, userId);
    }

    @Override
    @Transactional
    public void deleteRadarChart(Long id, Long userId) {
        RadarChart chart = getAndValidateRadarChart(id, userId);

        // 删除系列数据
        List<DataSeries> seriesList = dataSeriesMapper.selectList(
                new LambdaQueryWrapper<DataSeries>()
                        .eq(DataSeries::getRadarChartId, id)
        );

        for (DataSeries series : seriesList) {
            seriesDataMapper.delete(
                    new LambdaQueryWrapper<SeriesData>()
                            .eq(SeriesData::getSeriesId, series.getId())
            );
        }

        // 删除系列
        dataSeriesMapper.delete(
                new LambdaQueryWrapper<DataSeries>()
                        .eq(DataSeries::getRadarChartId, id)
        );

        // 删除维度
        dimensionMapper.delete(
                new LambdaQueryWrapper<Dimension>()
                        .eq(Dimension::getRadarChartId, id)
        );

        // 删除图表
        radarChartMapper.deleteById(id);
    }

    @Override
    @Transactional
    public SeriesResult addDataSeries(Long radarChartId, Long userId, AddDataSeriesParam param) {
        getAndValidateRadarChart(radarChartId, userId);

        // 检查系列名称是否重复
        long count = dataSeriesMapper.selectCount(
                new LambdaQueryWrapper<DataSeries>()
                        .eq(DataSeries::getRadarChartId, radarChartId)
                        .eq(DataSeries::getName, param.getName())
        );
        if (count > 0) {
            throw new BadRequestException("系列名称已存在");
        }

        // 获取所有维度
        List<Dimension> dimensions = dimensionMapper.selectList(
                new LambdaQueryWrapper<Dimension>()
                        .eq(Dimension::getRadarChartId, radarChartId)
        );

        // 创建数据系列
        DataSeries series = new DataSeries();
        series.setRadarChartId(radarChartId);
        series.setName(param.getName());
        dataSeriesMapper.insert(series);

        // 创建数据点
        Map<Long, Dimension> dimMap = dimensions.stream()
                .collect(Collectors.toMap(Dimension::getId, d -> d));

        for (AddDataSeriesParam.SeriesDataParam dataParam : param.getData()) {
            Dimension dim = dimMap.get(dataParam.getDimensionId());
            if (dim == null) {
                throw new BadRequestException("维度不存在: " + dataParam.getDimensionId());
            }

            // 验证数据值范围
            if (dataParam.getValue() < dim.getMinValue() || dataParam.getValue() > dim.getMaxValue()) {
                throw new BadRequestException(
                        String.format("维度[%s]的值必须在%.2f到%.2f之间",
                                dim.getName(), dim.getMinValue(), dim.getMaxValue())
                );
            }

            SeriesData data = new SeriesData();
            data.setSeriesId(series.getId());
            data.setDimensionId(dataParam.getDimensionId());
            data.setValue(dataParam.getValue());
            seriesDataMapper.insert(data);
        }

        return buildSeriesResult(series);
    }

    @Override
    public List<SeriesResult> getDataSeries(Long radarChartId, Long userId) {
        getAndValidateRadarChart(radarChartId, userId);

        List<DataSeries> seriesList = dataSeriesMapper.selectList(
                new LambdaQueryWrapper<DataSeries>()
                        .eq(DataSeries::getRadarChartId, radarChartId)
                        .orderByDesc(DataSeries::getCreateTime)
        );

        List<SeriesResult> results = new ArrayList<>();
        for (DataSeries series : seriesList) {
            results.add(buildSeriesResult(series));
        }
        return results;
    }

    @Override
    @Transactional
    public void updateSeriesName(Long radarChartId, Long seriesId, Long userId, UpdateSeriesNameParam param) {
        getAndValidateRadarChart(radarChartId, userId);

        DataSeries series = dataSeriesMapper.selectById(seriesId);
        if (series == null || !series.getRadarChartId().equals(radarChartId)) {
            throw new ResourceNotFoundException("数据系列不存在");
        }

        // 检查新名称是否与其他系列重复
        long count = dataSeriesMapper.selectCount(
                new LambdaQueryWrapper<DataSeries>()
                        .eq(DataSeries::getRadarChartId, radarChartId)
                        .eq(DataSeries::getName, param.getName())
                        .ne(DataSeries::getId, seriesId)
        );
        if (count > 0) {
            throw new BadRequestException("系列名称已存在");
        }

        series.setName(param.getName());
        dataSeriesMapper.updateById(series);
    }

    @Override
    @Transactional
    public void deleteSeries(Long radarChartId, Long seriesId, Long userId) {
        getAndValidateRadarChart(radarChartId, userId);

        DataSeries series = dataSeriesMapper.selectById(seriesId);
        if (series == null || !series.getRadarChartId().equals(radarChartId)) {
            throw new ResourceNotFoundException("数据系列不存在");
        }

        // 删除系列数据
        seriesDataMapper.delete(
                new LambdaQueryWrapper<SeriesData>()
                        .eq(SeriesData::getSeriesId, seriesId)
        );

        // 删除系列
        dataSeriesMapper.deleteById(seriesId);
    }

    @Override
    @Transactional
    public void updateSeriesData(Long radarChartId, Long seriesId, Long userId, UpdateSeriesDataParam param) {
        getAndValidateRadarChart(radarChartId, userId);

        DataSeries series = dataSeriesMapper.selectById(seriesId);
        if (series == null || !series.getRadarChartId().equals(radarChartId)) {
            throw new ResourceNotFoundException("数据系列不存在");
        }

        // 获取所有维度
        List<Dimension> dimensions = dimensionMapper.selectList(
                new LambdaQueryWrapper<Dimension>()
                        .eq(Dimension::getRadarChartId, radarChartId)
        );

        Map<Long, Dimension> dimMap = dimensions.stream()
                .collect(Collectors.toMap(Dimension::getId, d -> d));

        // 删除旧数据
        seriesDataMapper.delete(
                new LambdaQueryWrapper<SeriesData>()
                        .eq(SeriesData::getSeriesId, seriesId)
        );

        // 插入新数据
        for (AddDataSeriesParam.SeriesDataParam dataParam : param.getData()) {
            Dimension dim = dimMap.get(dataParam.getDimensionId());
            if (dim == null) {
                throw new BadRequestException("维度不存在: " + dataParam.getDimensionId());
            }

            // 验证数据值范围
            if (dataParam.getValue() < dim.getMinValue() || dataParam.getValue() > dim.getMaxValue()) {
                throw new BadRequestException(
                        String.format("维度[%s]的值必须在%.2f到%.2f之间",
                                dim.getName(), dim.getMinValue(), dim.getMaxValue())
                );
            }

            SeriesData data = new SeriesData();
            data.setSeriesId(seriesId);
            data.setDimensionId(dataParam.getDimensionId());
            data.setValue(dataParam.getValue());
            seriesDataMapper.insert(data);
        }
    }

    @Override
    public ChartDataResult getChartData(Long id, Long userId) {
        RadarChart chart = getAndValidateRadarChart(id, userId);

        // 获取维度（按顺序排序）
        List<Dimension> dimensions = dimensionMapper.selectList(
                new LambdaQueryWrapper<Dimension>()
                        .eq(Dimension::getRadarChartId, id)
                        .orderByAsc(Dimension::getOrderIndex)
        );

        // 获取所有系列
        List<DataSeries> seriesList = dataSeriesMapper.selectList(
                new LambdaQueryWrapper<DataSeries>()
                        .eq(DataSeries::getRadarChartId, id)
        );

        // 构建维度名称列表
        List<String> dimensionNames = new ArrayList<>();
        for (Dimension dim : dimensions) {
            dimensionNames.add(dim.getName());
        }

        // 构建系列数据
        Map<Long, Dimension> dimMap = dimensions.stream()
                .collect(Collectors.toMap(Dimension::getId, d -> d));

        List<ChartDataResult.SeriesDataResult> seriesDataResults = new ArrayList<>();
        for (DataSeries series : seriesList) {
            ChartDataResult.SeriesDataResult sdr = new ChartDataResult.SeriesDataResult();
            sdr.setName(series.getName());

            // 获取系列数据并按维度顺序排列
            Map<Long, Double> dataMap = new HashMap<>();
            List<SeriesData> dataList = seriesDataMapper.selectList(
                    new LambdaQueryWrapper<SeriesData>()
                            .eq(SeriesData::getSeriesId, series.getId())
            );
            for (SeriesData sd : dataList) {
                dataMap.put(sd.getDimensionId(), sd.getValue());
            }

            List<Double> values = new ArrayList<>();
            for (Dimension dim : dimensions) {
                values.add(dataMap.getOrDefault(dim.getId(), 0.0));
            }
            sdr.setData(values);

            seriesDataResults.add(sdr);
        }

        ChartDataResult result = new ChartDataResult();
        result.setDimensions(dimensionNames);
        result.setSeries(seriesDataResults);

        return result;
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

    private RadarChartResult buildRadarChartResult(RadarChart chart, List<Dimension> dimensions, List<DataSeries> seriesList) {
        RadarChartResult result = new RadarChartResult();
        result.setId(chart.getId());
        result.setTitle(chart.getTitle());
        result.setDescription(chart.getDescription());
        result.setCreateTime(chart.getCreateTime());
        result.setUpdateTime(chart.getUpdateTime());

        // 转换维度
        List<DimensionResult> dimResults = new ArrayList<>();
        List<Dimension> sortedDimensions = new ArrayList<>(dimensions);
        sortedDimensions.sort(Comparator.comparing(Dimension::getOrderIndex));
        for (Dimension dim : sortedDimensions) {
            DimensionResult dr = new DimensionResult();
            dr.setId(dim.getId());
            dr.setName(dim.getName());
            dr.setMinValue(dim.getMinValue());
            dr.setMaxValue(dim.getMaxValue());
            dr.setOrderIndex(dim.getOrderIndex());
            dimResults.add(dr);
        }
        result.setDimensions(dimResults);

        // 转换系列
        List<SeriesResult> seriesResults = new ArrayList<>();
        for (DataSeries series : seriesList) {
            seriesResults.add(buildSeriesResult(series));
        }
        result.setSeries(seriesResults);

        return result;
    }

    private SeriesResult buildSeriesResult(DataSeries series) {
        SeriesResult result = new SeriesResult();
        result.setId(series.getId());
        result.setName(series.getName());
        result.setCreateTime(series.getCreateTime());

        // 加载系列数据
        List<SeriesData> dataList = seriesDataMapper.selectList(
                new LambdaQueryWrapper<SeriesData>()
                        .eq(SeriesData::getSeriesId, series.getId())
        );

        Map<Long, Double> dataMap = new HashMap<>();
        for (SeriesData sd : dataList) {
            dataMap.put(sd.getDimensionId(), sd.getValue());
        }
        result.setData(dataMap);

        return result;
    }
}
