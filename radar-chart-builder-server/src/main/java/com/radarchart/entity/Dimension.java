package com.radarchart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("dimension")
public class Dimension {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long radarChartId;
    private String name;
    private Double minValue;
    private Double maxValue;
    private Integer orderIndex;
}
