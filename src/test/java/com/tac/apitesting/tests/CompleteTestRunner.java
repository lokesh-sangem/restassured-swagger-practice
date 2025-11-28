package com.tac.apitesting.tests;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompleteTestRunner {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        TestListenerAdapter tla = new TestListenerAdapter();
        //create suite
        XmlSuite suite = new XmlSuite();
        suite.setName("Complete AltoroMutual API Test Suite");
        suite.setParallel(XmlSuite.ParallelMode.TESTS);
        suite.setThreadCount(3);

        //define test classes in order
        List<Class> testClasses = Arrays.asList(
          LoginAPITest.class,
          AccountAPITest.class,
          TransferAPITest.class,
          FeedBackAPITest.class,
          AdminAPITest.class,
          LogOutAPITest.class
        );
        //create tests
        List<XmlTest> tests= new ArrayList<>();
        for(Class testClass : testClasses){
            XmlTest test = new XmlTest(suite);
            test.setName(testClass.getSimpleName());
            test.setXmlClasses(Arrays.asList(new XmlClass(testClass.getName())));
            tests.add(test);
        }

        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);
        testng.addListener(tla);

        System.out.println("Starting Complete API Test Suite...");
        testng.run();

        System.out.println("Test Suite Execution Completed!");
        System.out.println("Passed: " + tla.getPassedTests().size());
        System.out.println("Failed: " + tla.getFailedTests().size());
        System.out.println("Skipped: " + tla.getSkippedTests().size());



    }
}
