package com.radarchart.dto.param;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class UpdateSeriesDataParam {

    @NotEmpty(message = "数据不能为空")
    private List<AddDataSeriesParam.SeriesDataParam> data;

    public List<AddDataSeriesParam.SeriesDataParam> getData() {
        return data;
    }

    public void setData(List<AddDataSeriesParam.SeriesDataParam> data) {
        this.data = data;
    }
}
