package com.tac.apitesting.tests;

import com.tac.apitesting.base.BaseTest;
import com.tac.apitesting.config.TestConfig;
import com.tac.apitesting.models.Transfer;
import com.tac.apitesting.utils.AccountUtils;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TransferAPITest extends BaseTest {

       private String fromAccount;
       private String toAccount;
//       private String transferAmount;

       @BeforeClass
      public void setupTransferTests(){
           authToken = loginAndGetToken(TestConfig.VALID_USERNAME,TestConfig.VALID_PASSWORD);
           fromAccount = AccountUtils.getValidAccountNumber();
           toAccount = AccountUtils.getAnotherAccountNumber(fromAccount);
       }
     @Test(priority = 1,description="Test successful fund transfer between account")
    public void testSuccessfulFundTransfer(){
         Transfer transferPayload = new Transfer(toAccount,fromAccount,"100.00");
         getAuthenticatedRequest()
                 .body(transferPayload)
                 .when()
                 .post("/transfer")
                 .then()
                 .statusCode(200)
                 .body("success",equalTo(true))
                 .body("message",containsString("successfully"))
         .body("fromAccount",equalTo(fromAccount))
                 .body("toAccount",equalTo(toAccount))
                 .body("amount",equalTo("100.00"));



     }

     @Test(priority = 2,description = "Test transfer with invalid from account")
    public void testTransferWithInvalidFromAccount(){
           Transfer transferPayload = new Transfer(toAccount,TestConfig.INVALID_ACCOUNT,"50.00");
           getAuthenticatedRequest()
                   .body(transferPayload)
                   .when()
                   .post("/transfer")
                   .then().statusCode(400).body("success",equalTo(false));

     }
     @Test(priority = 3 ,description = "Test transfer with invalid to account")
    public void testTransferWithInvalidToAccount(){
         Transfer transferPayload = new Transfer(TestConfig.INVALID_ACCOUNT,fromAccount,"50.00");
         getAuthenticatedRequest()
                 .body(transferPayload)
                 .when()
                 .post("/transfer")
                 .then().statusCode(400).body("success",equalTo(false));
     }
     @Test(priority = 4,description="Test transfer with zero amount")
    public void testTransferWithZeroAmount(){
           Transfer transferPayload = new Transfer(toAccount,fromAccount,"0.00");
           getAuthenticatedRequest()
                   .body(transferPayload)
                   .when()
                   .post("/transfer")
                   .then().statusCode(400).body("success",equalTo(false));
     }
     @Test(priority = 5 ,description = "Test transfer with negative amount")
    public void testTransferWithNegativeAmount(){
           Transfer transferPayload = new Transfer(toAccount,fromAccount,"-50.00");
           getAuthenticatedRequest()
                   .body(transferPayload)
                   .when()
                   .post("/transfer")
                   .then().statusCode(400).body("success",equalTo(false));
     }
     @Test(priority = 6 ,description = "Test transfer with same accounts")
    public void testTransferWithSameAccounts(){
           Transfer transferPayload = new Transfer(fromAccount,fromAccount,"100.00");
           getAuthenticatedRequest()
                   .body(transferPayload)
                   .when()
                   .post("/transfer")
                   .then().statusCode(400).body("success",equalTo(false));
     }
     @Test(priority = 7 ,description ="Test transfer without authentication" )
     public void testTransferWithoutAuthentication() {
         Transfer transferPayload = new Transfer(toAccount, fromAccount, "100.00");
         given().spec(requestSpec)
                 .body(transferPayload)
                 .when()
                 .post("/transfer")
                 .then().statusCode(401);


     }

    @Test(priority = 8, description = "Test transfer with missing fields")
    public void testTransferWithMissingFields() {
        //test with missing toAccount
        Transfer invalidTransfer = new Transfer();
        invalidTransfer.setFromAccount(fromAccount);
        invalidTransfer.setTransferAmount("100.00");

        getAuthenticatedRequest()
                .body(invalidTransfer)
                .when().post("/transfer")
                .then()
                .statusCode(400);
    }
    @Test(priority = 9,description ="Test transfer with insufficient funds")
    public void testTransferInsufficientFunds(){
           Transfer transferPayload = new Transfer(toAccount,fromAccount,"9999999.00");
           getAuthenticatedRequest()
                   .body(transferPayload)
                   .when()
                   .post("/transfer")
                   .then().statusCode(400)
                   .body("success",equalTo(false))
                   .body("message",anyOf(
                           containsString("insufficient "),
                           containsString("funds")
                   ));

    }

     @Test(priority=10,description = "Test transfer with large amount")
    public void testTransferWithLargeAmount(){
           Transfer transferPayload = new Transfer(toAccount,fromAccount,"1000000.00");
           getAuthenticatedRequest()
                   .body(transferPayload)
                   .when()
                   .post("/transfer")
                   .then().statusCode(200); //should succeed if within limits
     }
}
