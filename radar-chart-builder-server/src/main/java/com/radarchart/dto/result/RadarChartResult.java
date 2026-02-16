package com.radarchart.dto.result;

import java.time.LocalDateTime;
import java.util.List;

public class RadarChartResult {
    private Long id;
    private String title;
    private String description;
    private List<DimensionResult> dimensions;
    private List<SeriesResult> series;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DimensionResult> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<DimensionResult> dimensions) {
        this.dimensions = dimensions;
    }

    public List<SeriesResult> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesResult> series) {
        this.series = series;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
