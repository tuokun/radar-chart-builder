package com.radarchart.dto.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class AddDataSeriesParam {

    @NotBlank(message = "系列名称不能为空")
    @Size(max = 50, message = "系列名称长度不能超过50")
    private String name;

    @NotEmpty(message = "数据不能为空")
    private List<SeriesDataParam> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SeriesDataParam> getData() {
        return data;
    }

    public void setData(List<SeriesDataParam> data) {
        this.data = data;
    }

    public static class SeriesDataParam {
        private Long dimensionId;

        private Double value;

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
    }
}
