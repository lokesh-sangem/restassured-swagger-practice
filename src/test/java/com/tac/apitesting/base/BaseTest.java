package com.tac.apitesting.base;
import com.tac.apitesting.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static io.restassured.RestAssured.*;

import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected RequestSpecification requestSpec;
    protected Map<String,Object> testContext;
    protected String authToken;

    @BeforeClass
    public void setupClass(){
        RestAssured.baseURI = TestConfig.BASE_URL;
        requestSpec = TestConfig.getRequestSpec();
    }

    @BeforeMethod
    public void setupMethod(){
        testContext = new HashMap<>();
        authToken = null;
    }

    protected String loginAndGetToken(String username,String password){
     Map<String,String> loginPayload = new HashMap<>();
     loginPayload.put("username",username);
     loginPayload.put("password",password);

     Response response = given()
             .spec(requestSpec)
             .body(loginPayload)
             .when()
             .post("/login")
             .then()
             .extract()
             .response();
        if(response.statusCode()==200){
            return response.jsonPath().getString("Authorization");
        }
        return null;
    }

    protected RequestSpecification getAuthenticatedRequest(){
        if(authToken  ==null){
         authToken = loginAndGetToken(TestConfig.VALID_USERNAME,TestConfig.VALID_PASSWORD);
        }
        return given()
                .spec(requestSpec)
                .header("Authorization",authToken);

    }


}
