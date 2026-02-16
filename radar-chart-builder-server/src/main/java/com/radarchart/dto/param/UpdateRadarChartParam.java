package com.radarchart.dto.param;

import jakarta.validation.constraints.Size;

public class UpdateRadarChartParam {

    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

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
}
