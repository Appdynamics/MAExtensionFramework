package com.appdynamics;

import com.appdynamics.aggregators.IMetricAggregator;
import com.appdynamics.models.Collector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;

public class Monitor extends AManagedMonitor {

	private static final Logger logger = Logger.getLogger(Monitor.class);

	/**
	 * Main execution method that uploads the metrics to the AppDynamics Controller.
	 * This method is called directly by the Machine Agent.
	 */
	public TaskOutput execute(Map<String, String> arg0, TaskExecutionContext arg1) {
		Gson gson = new Gson();
		List<Collector> collectors;

		String fileName = arg0.get("config-file");
		InputStreamReader inputStreamReader;

		int metricCount = 0;
		int successfullyPrintedMetrics = 0;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
			collectors = gson.fromJson(inputStreamReader, new TypeToken<ArrayList<Collector>>(){}.getType());
			if (collectors != null) {
				for (Collector collector : collectors) {
					logger.info("collector_" + metricCount++ + " " + collector);
					try {
						Class<?> clazz = Class.forName(collector.getAggregatorClass());
						IMetricAggregator metricAggregator = (IMetricAggregator) clazz.newInstance();
						List<Object> list = collector.getArgs();
						int metricVal = metricAggregator.computeMetric(list);
						if (!StringUtils.isBlank(collector.getMetricPath())) {
							MetricWriter metricWriter = this.getMetricWriter(collector.getMetricPath(),
									MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE,
									MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT,
									MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE);
							logger.info("Printing metric: " + collector.getMetricPath() + "=" + metricVal);
							metricWriter.printMetric(String.valueOf(metricVal));
							successfullyPrintedMetrics++;
						} else {
							logger.error("Invalid metric path specified for " + collector);
						}
					} catch (InstantiationException | IllegalAccessException e) {
						logger.error("Unable to instantiate class with name " + collector.getAggregatorClass());
					} catch (ClassNotFoundException e) {
						logger.error("Could not find aggregator class with name " + collector.getAggregatorClass());
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported encoding for file with name " + fileName);
		} catch (FileNotFoundException e) {
			logger.error("Could not find config file with name " + fileName);
		}
		System.out.println("Metric Upload Complete. " + successfullyPrintedMetrics + " successful, " + (metricCount - successfullyPrintedMetrics) + " unsuccessful");
		return new TaskOutput("Metric Upload Complete. " + successfullyPrintedMetrics + " successful, " + (metricCount - successfullyPrintedMetrics) + " unsuccessful");
	}
}
