package com.appdynamics.models;

import lombok.*;

import java.util.List;

@Data
public class Settings {

    private MetricProcessingQualifiers metricProcessingQualifiers;
	List<Collector> collectors;

	@Data
	public static class Collector {
        private String metricPath;
        private String aggregatorClass;
        private List<Object> args;
    }

	@Data
	public static class MetricProcessingQualifiers {
        private String metricAggregationType;
        private String metricTimeRollupType;
        private String metricClusterRollupType;
    }

}
