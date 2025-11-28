package com.tac.apitesting.utils;

import com.tac.apitesting.base.BaseTest;
import com.tac.apitesting.config.TestConfig;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.when;

public class AccountUtils extends BaseTest {
    public static String getValidAccountNumber(){
     Response response = getAuthenticatedRequest()
             .when()
             .get("/account")
             .then()
             .extract()
             .response();
    List<Map<String,Object>> accounts = response.jsonPath().getList("$");
    return accounts.isEmpty() ? TestConfig.FROM_ACCOUNT :accounts.get(0).get("id").toString();
    }

    public static String getAnotherAccountNumber(String excludeAccount){
        Response response = getAuthenticatedRequest()
                .when()
                .get("/account")
                .then().extract().response();
        List<Map<String,Object>>accounts = response.jsonPath().getList("$");
        for(Map<String,Object>account:accounts){
            String accountId = account.get("id").toString();
            if(!accountId.equals(excludeAccount)){
                return accountId;
            }
        }
        return TestConfig.TO_ACCOUNT; //Fallback
    }


}
