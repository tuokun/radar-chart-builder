package com.radarchart.dto.result;

import java.util.List;

public class ChartDataResult {
    private List<String> dimensions; // 维度名称列表，按orderIndex排序
    private List<SeriesDataResult> series;

    public List<String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions) {
        this.dimensions = dimensions;
    }

    public List<SeriesDataResult> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesDataResult> series) {
        this.series = series;
    }

    public static class SeriesDataResult {
        private String name;
        private List<Double> data; // 按维度顺序排列的值

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Double> getData() {
            return data;
        }

        public void setData(List<Double> data) {
            this.data = data;
        }
    }
}
