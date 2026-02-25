package com.radarchart.dto.result;

import java.time.LocalDateTime;
import java.util.List;

public class SeriesResult {
    private Long id;
    private String name;
    private String color;  // 前端使用的颜色
    private Integer displayOrder;  // 前端使用的显示顺序
    private List<SeriesDataResult> data;  // 改为数组格式
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<SeriesDataResult> getData() {
        return data;
    }

    public void setData(List<SeriesDataResult> data) {
        this.data = data;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
