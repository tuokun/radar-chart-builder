package com.radarchart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("series_data")
public class SeriesData {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long seriesId;
    private Long dimensionId;
    private Double value;
}
