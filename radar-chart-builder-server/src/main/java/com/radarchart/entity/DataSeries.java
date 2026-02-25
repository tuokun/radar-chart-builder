package com.radarchart.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@TableName("data_series")
public class DataSeries {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long radarChartId;
    private String name;
    private String color;  // 系列颜色

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSeries that = (DataSeries) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DataSeries{" +
                "id=" + id +
                ", radarChartId=" + radarChartId +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
