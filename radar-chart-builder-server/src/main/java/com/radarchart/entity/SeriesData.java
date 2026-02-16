package com.radarchart.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Objects;

@TableName("series_data")
public class SeriesData {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long seriesId;
    private Long dimensionId;
    private Double value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public Long getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(Long dimensionId) {
        this.dimensionId = dimensionId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeriesData that = (SeriesData) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SeriesData{" +
                "id=" + id +
                ", seriesId=" + seriesId +
                ", dimensionId=" + dimensionId +
                ", value=" + value +
                '}';
    }
}
