package com.tac.apitesting.tests;

import com.tac.apitesting.base.BaseTest;
import com.tac.apitesting.config.TestConfig;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LogOutAPITest extends BaseTest {

    @Test(priority = 1, description = "Test successful logout")
    public void testSucessfulLogout() {
    authToken=loginAndGetToken(TestConfig.VALID_USERNAME,TestConfig.VALID_PASSWORD);
    given().spec(requestSpec).when().get("/logout")
            .then().statusCode(200)
            .body("success",equalTo(true))
            .body("message",anyOf(
                    containsString("logout"),containsString("success")
            ));
    }

    @Test(priority = 2, description = "Test logout without active session")
    public void testLogoutWithoutActiveSession() {
      given()
              .spec(requestSpec)
              .when()
              .get("/logout")
              .then()
              .statusCode(200);//logout should still succes even without active session
    }

    @Test(priority = 3, description = "Test verify session is invalid after logout")
    public void testSessionInvalidAfterLogout() {
     //login and then logout
        authToken = loginAndGetToken(TestConfig.VALID_USERNAME, TestConfig.VALID_PASSWORD);
        given()
                .spec(requestSpec)
                .when()
                .get("/logout")
                .then().statusCode(200);
        //try to access protected resource
        given()
                .spec(requestSpec)
                .header("Authorization",authToken)
                .when()
                .get("/account")
                .then().statusCode(401);//should be unauthorized after logout.
    }

    @Test(priority = 4, description = "Test multiple consecutive logouts")
    public void testMultipleConsecutiveLogouts() {
   //first logout
        given().spec(requestSpec)
                .when().get("/logout")
                .then().statusCode(200);
        //second logout
        given().spec(requestSpec)
                .when().get("/logout")
                .then().statusCode(200);//should still succeed

    }


}
