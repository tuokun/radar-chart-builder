package com.radarchart.dto.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateSeriesNameParam {

    @NotBlank(message = "系列名称不能为空")
    @Size(max = 50, message = "系列名称长度不能超过50")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
