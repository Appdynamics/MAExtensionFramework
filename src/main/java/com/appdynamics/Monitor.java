package com.appdynamics;

import com.appdynamics.aggregators.IMetricAggregator;
import com.appdynamics.models.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
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
        InputStreamReader inputStreamReader;

        Settings settings;
        List<Settings.Collector> collectors;
        int metricCount = 0;
		int successfullyPrintedMetrics = 0;
		try {
            inputStreamReader = new InputStreamReader(new FileInputStream(arg0.get("config-file")), StandardCharsets.UTF_8);
            settings = gson.fromJson(inputStreamReader, Settings.class);

			collectors = settings.getCollectors();

			if (collectors != null) {
				for (Settings.Collector collector : collectors) {
					logger.info("collector_" + metricCount++ + " " + collector);
					try {
						Class<?> clazz = Class.forName(collector.getAggregatorClass());
						IMetricAggregator metricAggregator = (IMetricAggregator) clazz.newInstance();
						List<Object> list = collector.getArgs();
						int metricVal = metricAggregator.computeMetric(list);
						if (!StringUtils.isBlank(collector.getMetricPath())) {
							MetricWriter metricWriter = this.getMetricWriter(collector.getMetricPath(),
									settings.getMetricProcessingQualifiers().getMetricAggregationType(),
									settings.getMetricProcessingQualifiers().getMetricTimeRollupType(),
									settings.getMetricProcessingQualifiers().getMetricClusterRollupType());
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
		} catch (FileNotFoundException e) {
			logger.error("Could not find config file with name.");
		}
		System.out.println("Metric Upload Complete. " + successfullyPrintedMetrics + " successful, " + (metricCount - successfullyPrintedMetrics) + " unsuccessful");
		return new TaskOutput("Metric Upload Complete. " + successfullyPrintedMetrics + " successful, " + (metricCount - successfullyPrintedMetrics) + " unsuccessful");
	}
}
