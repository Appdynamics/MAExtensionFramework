package com.appdynamics;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

/**
 * Target this class for testing purposes only. When deployed to a Machine Agent this class will not be used at all.
 */
public class Main {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Monitor monitor = new Monitor();
        Map<String, String> arg0 = new HashMap<>();

        arg0.put("config-file", "src/main/resources/conf/config.json");
        monitor.execute(arg0, null);
    }

}
