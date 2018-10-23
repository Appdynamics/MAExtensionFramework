package com.appdynamics.models;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @ToString
public class Collector {

	private String metricPath;
	private String aggregatorClass;
	private List<Object> args;

}
