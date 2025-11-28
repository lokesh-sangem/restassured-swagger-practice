package com.tac.apitesting.tests;

import com.tac.apitesting.base.BaseTest;
import com.tac.apitesting.config.TestConfig;
import com.tac.apitesting.models.ChangePassword;
import com.tac.apitesting.models.NewUser;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AdminAPITest extends BaseTest {
    private String newUsername;

    @BeforeClass
    public void setupAdminTests(){
        authToken = loginAndGetToken(TestConfig.VALID_USERNAME,TestConfig.VALID_PASSWORD);
        newUsername = TestConfig.generateUniqueUsername();
    }

    @Test(priority = 1 ,description = "Test add new user with valid data")
    public void testAddNewUserSuccessfully(){
      String strongPassword = TestConfig.generateStrongPassword();
      NewUser newUserPayload = new NewUser(
              "Test","User",newUsername,strongPassword,strongPassword
      );

      Response response = getAuthenticatedRequest()
              .body(newUserPayload)
              .when().post("/admin/addUser")
              .then().statusCode(200)
              .body("success",equalTo(true))
              .body("message",anyOf(
                      containsString("created"),containsString("success")
              ))
              .body("username",equalTo(newUsername))
              .extract().response();

      testContext.put("newUsername",newUsername);
    }

    @Test(priority = 2 ,description = "Test add new user with existing username")
    public void testAddUserWithExistingUsername(){
    NewUser newUserPayload = new NewUser(
            "Another",
            "User",
            TestConfig.VALID_USERNAME,//existing username
            "StrongP@ss123",
            "StrongP@ss123"
    );

    getAuthenticatedRequest()
            .body(newUserPayload)
            .when()
            .post("/admin/addUser")
            .then()
            .statusCode(400)
            .body("success",equalTo(false));

    }

    @Test(priority = 3 ,description = "Test add new user with weak password")
    public void testAddUserWithWeakPassword(){
     NewUser newUserPayload = new NewUser(
             "Weak","Password",TestConfig.generateUniqueUsername(),"weak"//Too weak
              ,"weak");
     getAuthenticatedRequest()
             .body(newUserPayload)
             .when()
             .post("/admin/addUser")
             .then()
             .statusCode(400)
             .body("success",equalTo(false));
    }

    @Test(priority = 4 ,description ="Test add user with mismatched passwords")
    public void testAddUserWithMismatchedPasswords(){
     NewUser newUserPayload = new NewUser(
             "Mismatch","User",TestConfig.generateUniqueUsername(),"Password123!","DifferentPassword123!"//Different from password1
     );
     getAuthenticatedRequest()
             .body(newUserPayload)
             .when()
             .post("/admin/addUser")
             .then()
             .statusCode(400)
             .body("success",equalTo(false));
    }

    @Test(priority =5 ,description = "Test add user with missing required fields")
    public void testAddUserWithMissingFields(){
   NewUser newUserPayload = new NewUser();
        newUserPayload.setFirstname("Partial");
        newUserPayload.setLastname("User");
        //missing username and passwords

        getAuthenticatedRequest()
                .body(newUserPayload)
                .when()
                .post("/admin/addUser")
                .then().statusCode(400);

    }

    @Test(priority =11 ,description ="Test add user with very long username")
    public void testAddUserWithLongUsername(){
       String longUsername = "verylongusername_" + "a".repeat(50);
       NewUser newUserPayload = new NewUser(
               "Long","Username",longUsername,"StrongP@ss123!","StrongP@ss123!"
       );
       getAuthenticatedRequest()
               .body(newUserPayload)
               .when()
               .post("/admin/addUser")
               .then().statusCode(400);//should fail due to username length


    }

    @Test(priority =10 ,description ="Test admin operations without authentication")
    public void testAdminOperationsWithoutAuth(){
    NewUser newUserPayload = new NewUser(
            "Unauthorized","User",TestConfig.generateUniqueUsername(),"Password123!","Password123!"
    );

    given()
            .spec(requestSpec)
            .body(newUserPayload)
            .when()
            .post("/admin/addUser")
            .then()
            .statusCode(401);
    }

    @Test(priority =9 ,description ="Test change password with weak new password")
    public void testChangePasswordWithWeakPassword(){
   ChangePassword changePasswordPayload = new ChangePassword(TestConfig.VALID_USERNAME,"weak","weak");
   getAuthenticatedRequest()
           .body(changePasswordPayload)
           .when()
           .post("/admin/changePassword")
           .then()
           .statusCode(400)
           .body("success",equalTo(false));
    }

    @Test(priority =8,description = "Test change pasword with mismatched passwords")
    public void testChangePasswordWithMismatchedPasswords(){
   ChangePassword changePasswordPayload  = new ChangePassword(
           TestConfig.VALID_USERNAME,"Password123!","Different123!"// different from password1
   );
   getAuthenticatedRequest()
           .body(changePasswordPayload)
           .when()
           .post("/admin/changePassword")
           .then().statusCode(400).body("success",equalTo(false));
    }

    @Test(priority =7 ,description="Test change password for non-existent user")
    public void testChangePasswordForNonExistentUser(){
    ChangePassword changePasswordPayload = new ChangePassword("nonexistentuser","NewP@ss123","NewP@ss123");
    getAuthenticatedRequest().body(changePasswordPayload).when().post("/admin/changePassword").then().statusCode(400)
            .body("success",equalTo(false));

    }

    @Test(priority = 6 ,description ="Test change password successfully")
    public void testChangePasswordSuccessfully(){
        String newStrongPassword = "NewStrongP@ss123_"+System.currentTimeMillis();
        ChangePassword changePasswordPayload = new ChangePassword(
                TestConfig.VALID_USERNAME,newStrongPassword,newStrongPassword
        );
        getAuthenticatedRequest()
                .body(changePasswordPayload)
                .when()
                .post("/admin/changePassword")
                .then()
                .statusCode(200)
                .body("success",equalTo(true))
                .body("message",anyOf(containsString("changed"),containsString("updated")));
    }



}
