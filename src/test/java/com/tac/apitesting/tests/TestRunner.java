package com.tac.apitesting.tests;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        TestListenerAdapter tla  = new TestListenerAdapter();

        List<String>suites = new ArrayList<>();
        suites.add("src/test/java/testng.xml");
        testng.setTestSuites(suites);
        testng.addListener(tla);
        testng.run();
    }
}
