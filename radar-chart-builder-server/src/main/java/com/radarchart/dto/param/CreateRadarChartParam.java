package com.radarchart.dto.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateRadarChartParam {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    @NotEmpty(message = "至少需要3个维度")
    @Size(min = 3, message = "至少需要3个维度")
    private List<DimensionParam> dimensions;

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

    public List<DimensionParam> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<DimensionParam> dimensions) {
        this.dimensions = dimensions;
    }

    public static class DimensionParam {
        @NotBlank(message = "维度名称不能为空")
        @Size(max = 50, message = "维度名称长度不能超过50")
        private String name;

        private Double minValue = 0.0;

        private Double maxValue = 100.0;

        private Integer orderIndex;

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
    }
}
