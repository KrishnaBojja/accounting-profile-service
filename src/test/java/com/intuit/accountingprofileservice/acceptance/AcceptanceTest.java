package com.intuit.accountingprofileservice.acceptance;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = "src/test/resources", tags = {"~@ignore"})
public class AcceptanceTest {

}
