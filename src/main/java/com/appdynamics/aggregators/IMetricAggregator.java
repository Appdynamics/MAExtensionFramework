package com.appdynamics.aggregators;

import java.util.List;

public interface IMetricAggregator {

	int computeMetric(List<Object> argsOpt);

}
