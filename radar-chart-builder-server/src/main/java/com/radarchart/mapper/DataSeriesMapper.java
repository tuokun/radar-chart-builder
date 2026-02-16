package com.radarchart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.radarchart.entity.DataSeries;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataSeriesMapper extends BaseMapper<DataSeries> {
}
