package com.radarchart.dto.result;

import java.time.LocalDateTime;
import java.util.Map;

public class SeriesResult {
    private Long id;
    private String name;
    private Map<Long, Double> data; // dimensionId -> value
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

    public Map<Long, Double> getData() {
        return data;
    }

    public void setData(Map<Long, Double> data) {
        this.data = data;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
