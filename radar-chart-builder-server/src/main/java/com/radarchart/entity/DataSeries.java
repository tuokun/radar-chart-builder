package com.radarchart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("data_series")
public class DataSeries {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long radarChartId;
    private String name;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
