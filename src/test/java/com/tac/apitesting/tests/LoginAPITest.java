package com.tac.apitesting.tests;

import com.tac.apitesting.base.BaseTest;
import com.tac.apitesting.config.TestConfig;
import com.tac.apitesting.models.Login;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginAPITest extends BaseTest {
    @Test(description ="Test successful login with valid credentials")
    public void testSuccessfulLogin(){
     Login loginPayload = new Login(TestConfig.VALID_USERNAME,TestConfig.VALID_PASSWORD);
     Response response = given()
             .spec(requestSpec)
             .body(loginPayload)
             .when()
             .post("/login")
             .then()
             .statusCode(200)
             .body("Authorization",notNullValue())
             .body("Authorization",containsString("Bearer"))
             .extract()
             .response();

     authToken = response.jsonPath().getString("Authorization");
     testContext.put("authToken",authToken);
    }

    @Test(description ="Test login failure with invalid credentials")
    public void testLoginWithInvalidCredentials(){
        Login loginPayload = new Login(TestConfig.INVALID_USERNAME,TestConfig.INVALID_PASSWORD);
        given()
             .spec(requestSpec)
                .body(loginPayload)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("message",notNullValue());
    }

    @Test(description ="Test login with missing username")
    public void testLoginWithMissingUsername(){
        Login loginPayload = new Login();
        loginPayload.setPassword(TestConfig.VALID_PASSWORD);
        given()
                .spec(requestSpec)
                .body(loginPayload)
                .when()
                .post("/login")
                .then()
                .statusCode(400);
    }


    @Test(description="Test login with missing password")
    public void testLoginWithMissingPassword(){
       Login loginPayload = new Login();
       loginPayload.setUsername(TestConfig.VALID_USERNAME);
       given()
               .spec(requestSpec)
               .body(loginPayload)
               .when()
               .post("/login")
               .then().statusCode(400);

    }


    @Test(description ="Test login with empty username and password")
    public void testLoginWithEmptyCredentials(){
        Login loginPayload = new Login("","");
        given().spec(requestSpec).body(loginPayload)
                .when()
                .post("/login")
                .then().statusCode(400);
    }

    @Test(description ="Test check login status with valid token")
    public void testCheckLoginStatusWithValidToken(){
        //first login to get token
        String token = loginAndGetToken(TestConfig.VALID_USERNAME,TestConfig.VALID_PASSWORD);
        given().spec(requestSpec)
                .header("Authorization",token)
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .body(notNullValue());//user name should be returned
    }

    @Test(description="Test check login status with invalid token")
    public void testCheckLoginStatusWithInvalidToken(){
        given()
                .spec(requestSpec)
                .header("Authorization","Bearer invalidToken_123")
                .when()
                .get("/login")
                .then()
                .statusCode(401);

    }


    @Test(description = "Test check login status without token")
    public void testCheckLoginStatusWithoutToken(){
        given()
                .spec(requestSpec)
                .when()
                .get("/login")
                .then()
                .statusCode(401);
    }



}
