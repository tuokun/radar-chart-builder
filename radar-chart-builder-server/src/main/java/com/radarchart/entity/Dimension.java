package com.radarchart.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Objects;

@TableName("dimension")
public class Dimension {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long radarChartId;
    private String name;
    private Double minValue;
    private Double maxValue;
    private Integer orderIndex;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRadarChartId() {
        return radarChartId;
    }

    public void setRadarChartId(Long radarChartId) {
        this.radarChartId = radarChartId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimension dimension = (Dimension) o;
        return Objects.equals(id, dimension.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "id=" + id +
                ", radarChartId=" + radarChartId +
                ", name='" + name + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", orderIndex=" + orderIndex +
                '}';
    }
}
