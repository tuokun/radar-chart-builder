package com.radarchart.dto.param;

import jakarta.validation.constraints.Size;
import java.util.List;

public class UpdateRadarChartParam {

    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private List<SeriesParam> series;

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

    public List<SeriesParam> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesParam> series) {
        this.series = series;
    }

    public static class SeriesParam {
        private String id;  // 如果以 "new-" 开头，表示新系列；否则是现有系列ID
        @Size(max = 50, message = "系列名称长度不能超过50")
        private String name;
        private String color;
        private Integer displayOrder;
        private List<SeriesDataParam> data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
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

        public List<SeriesDataParam> getData() {
            return data;
        }

        public void setData(List<SeriesDataParam> data) {
            this.data = data;
        }
    }

    public static class SeriesDataParam {
        private String dimensionId;
        private Double value;

        public String getDimensionId() {
            return dimensionId;
        }

        public void setDimensionId(String dimensionId) {
            this.dimensionId = dimensionId;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }
}
