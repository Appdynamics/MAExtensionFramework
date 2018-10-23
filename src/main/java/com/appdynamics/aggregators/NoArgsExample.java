package com.appdynamics.aggregators;

import com.appdynamics.Monitor;
import org.apache.log4j.Logger;

import java.util.List;

public class NoArgsExample implements IMetricAggregator {

	private static final Logger logger = Logger.getLogger(Monitor.class);

	public int computeMetric(List<Object> argsOpt) {

//		do some work...
		int max = 6;
		int min = 0;
		int result = (int) ((Math.random() * ((max - min) + 1)) + min);
//		compute result...

		return result;
	}
}
