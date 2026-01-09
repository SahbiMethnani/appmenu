package com.example.appmenu.bdd;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.appmenu.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html, json:target/cucumber.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @Ignore")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
public class RunCucumberTest {
    // Ce runner exécute tous les scénarios Cucumber
    // Les rapports sont générés dans:
    // - target/cucumber-reports.html (HTML)
    // - target/cucumber.json (JSON pour intégrations)
}