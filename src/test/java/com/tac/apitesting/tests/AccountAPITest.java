package com.tac.apitesting.tests;

import com.tac.apitesting.base.BaseTest;
import com.tac.apitesting.config.TestConfig;
import com.tac.apitesting.models.Dates;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AccountAPITest extends BaseTest {
    private String accountNumber;

    @BeforeClass
    public void setupAccountTests(){
        //Login and get accounts to use in tests
        authToken = loginAndGetToken(TestConfig.VALID_USERNAME,TestConfig.VALID_PASSWORD);
    }

    @Test(priority = 1 ,description ="Test get all accounts for logged in user")
    public void testGetAllAccounts(){
        Response response = getAuthenticatedRequest()
                .when()
                .get("/account")
                .then()
                .statusCode(200)
                .body("$",not(empty()))
                .body("[0].id",notNullValue())
                .body("[0].name",notNullValue())
                .body("[0].balance",notNullValue())
                .extract().response();

        //store first account number for subsequent tests
        List<Map<String,Object>> accounts = response.jsonPath().getList("$");
        if(!accounts.isEmpty()){
            accountNumber = accounts.get(0).get("id").toString();
            testContext.put("accountNumber",accountNumber);
        }
    }

    @Test(priority =2,description ="Test get specific account details")
    public void testGetAccountDetails(){
      String accNumber = (String) testContext.get("accountNumber");
      getAuthenticatedRequest()
              .pathParam("accountNo",accNumber)
              .when()
              .get("/account/{accountNo}")
              .then()
              .statusCode(200)
              .body("id",equalTo(accNumber))
              .body("name",notNullValue())
              .body("balance",notNullValue())
              .body("accountType",notNullValue());
    }

    @Test(priority=3 ,description ="Test get account transactions")
    public void testGetAccountTransactions(){
        String accNumber = (String)testContext.get("accountNumber");
        getAuthenticatedRequest()
                .pathParam("accountNo",accNumber)
                .when()
                .get("/account/{accountNo}/transactions")
                .then()
                .statusCode(200)
                .body("$",not(empty()))
                .body("[0].accountId",notNullValue())
                .body("[0].type",notNullValue())
                .body("[0].amount",notNullValue())
                .body("size()",lessThanOrEqualTo(10)); // Should return last 10 transactions

    }

    @Test(priority =4 ,description ="Test get transactions between specific dates")
    public void testGetTransactionsByDateRange(){
        String accNumber = (String)testContext.get("accountNumber");
        Dates dateRange = new Dates("2023-01-01","2023-12-31");

        getAuthenticatedRequest()
                .pathParam("accountNo",accNumber)
                .body(dateRange)
                .when()
                .post("/account/{accountNo}/transactions")
                .then().statusCode(200)
                .body("$",notNullValue());
    }

    @Test(priority = 5 ,description ="Test transactions with invalid date range")
    public void testGetTransactionsWithInvalidDateRange(){
        String accNumber = (String) testContext.get("accountNumber");
        Dates invalidDateRange = new Dates("2023-12-31","2023-01-01");//end date before start date
        getAuthenticatedRequest()
                .pathParam("accountNo",accNumber)
                .body(invalidDateRange)
                .when()
                .post("/account/{accountNo}/transactions")
                .then().statusCode(400);//should return bad request
    }


    @Test(description = "Test get account details with invalid account number")
    public void testGetInvalidAccountDetails(){
        getAuthenticatedRequest()
                .pathParam("accountNo","99999")
                .when()
                .get("/account/{accountNo}")
                .then().statusCode(500);//internal server error for invalid account

    }


    @Test(description ="Test get accounts without authentication")
    public void testGetAccountsWithoutAuth(){
        given().spec(requestSpec)
                .when()
                .get("/account")
                .then()
                .statusCode(401);//unauthorized
    }

    @Test(description = "Test get account transactions without authentication")
     public void testGetTransactionsWithoutAuth(){
        String accNumber = (String) testContext.get("accountNumber");
        given().spec(requestSpec)
                .pathParam("accountNo",accNumber)
                .when().get("/account/{accountNo}/transactions")
                .then().statusCode(401);
    }




}
