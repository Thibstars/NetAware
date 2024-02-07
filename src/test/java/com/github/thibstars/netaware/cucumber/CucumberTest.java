package com.github.thibstars.netaware.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * @author Thibault Helsmoortel
 */
@Suite
@SuiteDisplayName("Cucumber Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("cucumber")
@ConfigurationParameter(key = io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@SuppressWarnings("java:S2187") // No need to add some tests to this class
public class CucumberTest {

}
