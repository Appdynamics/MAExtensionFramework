# MAExtensionFramework
---
The goal of this project was to lay out a concise framework for developing Machine Agent extensions. This process should be reasonably simple and require minimal configuration.
### Requirements
---
1. Maven required to build package
2. MachineAgent.jar you are targeting 
    - Can be found on http://download.appdynamics.com/ 
### Configuration
---
When configuring a Machine Agent Extension, there are only 2 variables to worry about.
1. Metric Path we would like our metric to be written to.
2. The integer values we would like to push.

To achieve this, we will define a config.json file of the following format.
```
[    
    {
        "metricPath"        : String,
        "aggregatorClass"   : String,
        "args":             : Array.of(Object)
    },
    ...
]
```
Where aggregatorClass is the fully qualified class name of a class which implements IMetricAggregator and args are the optional arguments to said class.

Each individual metric we push to the controller will have a corresponding JSONObject in the config.json file.

IMetricAggregator is simple to implement. Its only method is as follows.
```java
public int computeMetric(List<String> args);
```
The return value of this method will automatically be picked up and pushed to the controller according to the metricPath specified in config.json. 

### Assumptions
---
The Controller has various qualifiers for how it processes metrics with regard to aggregation, time rollup, and tier rollup. For the sake of simplicity this extension makes several assumptions about desired default behavior.

| Roll up Strategy | Description |
| ------ | ------ |
| METRIC_AGGREGATION_TYPE_AVERAGE | Default. Average of all reported values in that minute. |
| METRIC_TIME_ROLLUP_TYPE_AVERAGE | Average of all one-minute data points when adding it to the 10-minute or 60-minute granularity table. |
| METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL | Aggregates the metric value by averaging the metric values across each node in the tier. |
---
### Downloading, Building, and Deploying to Machine Agent
---
1. Clone repository
    ```sh
    git clone https://github.com/Appdynamics/MAExtensionFramework
    ```
2. Package with Maven
    ```sh
    mvn package
    ```
3. Grab the following files:
    - MAExtensionFramework.jar from target/
    - config.json from src/main/resources/conf/ 
    - monitor.xml from src/main/resources/conf/ 
4. Create a MAExtensionFramework folder in monitors/ folder on deployed Machine Agent and place all 3 above files inside
5. Restart Machine Agent

For support please email: bradley.hjelmar@appdynamics.com
