package com.tac.apitesting.config;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class TestConfig {
    //base url
    public static final String BASE_URL = "https://demo.testfire.net/api";
    //test data
    public static final String VALID_USERNAME = "jsmith";
    public static final String VALID_PASSWORD = "demo1234";
    public static final String INVALID_USERNAME = "invaliduser";
    public static final String INVALID_PASSWORD = "invalidpass";

    // test accounts for transfer
    public static final String FROM_ACCOUNT = "800002";
    public static final String TO_ACCOUNT = "800003";
    public static final String INVALID_ACCOUNT = "999999";

    public static RequestSpecification getRequestSpec(){
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public static ResponseSpecification getResponseSpec(){
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static String generateUniqueUsername(){
      return "testuser_"+System.currentTimeMillis();
    }

    public static String generateUniqueEmail(){
        return "test_"+System.currentTimeMillis()+"@test.com";
    }

    public static String generateStrongPassword(){
        return "StrongP@ss123_"+System.currentTimeMillis();
    }
}
