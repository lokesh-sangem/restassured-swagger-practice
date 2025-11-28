package com.tac.apitesting.tests;

import com.tac.apitesting.base.BaseTest;
import com.tac.apitesting.config.TestConfig;
import com.tac.apitesting.models.FeedBack;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class FeedBackAPITest extends BaseTest {
    private String feedbackId;
    @Test(priority=1 ,description = "Test submit feedback successfully")
    public void testSubmitFeedbackSuccessfully(){
       FeedBack  feedbackPayload = new FeedBack(
               "J Smith",
                "jsmtih@altoromutual.com",
                "Amazing web design",
                "I like the new look of your application");

       Response response = given()
               .spec(requestSpec)
               .body(feedbackPayload)
               .when()
               .post("/feedback")
               .then()
               .statusCode(200)
               .body("success",equalTo(true))
               .body("message",containsString("submitted"))
               .body("feedbackId",notNullValue())
               .extract().response();

       feedbackId = response.jsonPath().getString("feedbackId");
        testContext.put("feedbackId",feedbackId);

    }

    @Test(priority = 2 ,description = "Test submit feedback with invalid email")
    public void testSubmitFeedbackWithInvalidEmail(){
        FeedBack feedbackPayload = new FeedBack(
                "Jane Smith",
                "invalid-email",
                "Website Feedback",
                "The website could use some improvements in the navigation."
        );

        given()
                .spec(requestSpec)
                .body(feedbackPayload)
                .when()
                .post("/feedback/submit")
                .then()
                .statusCode(400)
                .body("success",equalTo(false));


    }

    @Test(priority = 3,description = "Test submit feedback with missing required fields")
    public void testSubmitFeedbackWithMissingRequiredFields(){
        FeedBack feedbackPayload = new FeedBack();
        feedbackPayload.setName("Lokesh");
        feedbackPayload.setEmail("test@test.com");
        //iam missing subject and message

        given()
                .spec(requestSpec)
                .body(feedbackPayload)
                .when()
                .post("/feedback/submit")
                .then().statusCode(400);
    }

    @Test(priority = 4,description ="Test submit feedback with empty payload")
    public void testSubmitFeedbackWithEmptyPayload(){
        FeedBack feedbackPayload = new FeedBack("","","","");
        given().spec(requestSpec)
                .body(feedbackPayload)
                .when()
                .post("/feedback/submit")
                .then().statusCode(400);

    }

    @Test(priority = 5,description ="Test submit feedback with special characters")
    public void testSubmitFeedbackWithSpecialCharacters(){
        FeedBack feedbackPayload = new FeedBack(
                "José Müller",
                "josé.müller@test.com",
                "¡Special Characters Test!",
                "Message with special chars: áéíóú ñ € £ ¥"
        );

        given()
                .spec(requestSpec)
                .body(feedbackPayload)
                .when()
                .post("/feedback/submit")
                .then().statusCode(200);
    }

    @Test(priority = 6 ,description ="Test submit feedback without authentication" )
    public void testRetrieveFeedbackWithoutAuthentication() {
        String storedFeedbackId = (String)testContext.get("feedbackId");
        given().spec(requestSpec)
                .pathParam("feedbackId",storedFeedbackId!=null ?storedFeedbackId:null)
                .when()
                .get("/feedback/{feedbackId}")
                .then().statusCode(401);

    }

    @Test(priority = 7 ,description="Test retrieve feedback with invalid Id")
    public void testRetrieveFeedbackWithInvalidId(){


        getAuthenticatedRequest()
                .pathParam("feedbackId","invalid-feedback-id-123")
                .when()
                .get("/feedback/{feedbackId}")
                .then().statusCode(400);
    }

    @Test(priority = 8,description ="Test retrieve feedback with valid ID")
    public void testRetrieveFeedbackWithValidId(){
        String storedFeedbackId = (String)testContext.get("feedbackId");
        if(storedFeedbackId!=null){
         getAuthenticatedRequest()
                 .pathParam("feedbackId",storedFeedbackId)
                 .when()
                 .get("/feedback/{feedbackId}")
                 .then()
                 .statusCode(200)
                 .body("id",equalTo(storedFeedbackId))
                 .body("name",notNullValue())
                 .body("email",notNullValue())
                 .body("subject",notNullValue())
                 .body("message",notNullValue());
        }

    }

    @Test(priority =9,description ="Test submit feedback with very long message")
    public void testSubmitFeedbackWithLongMessage(){
        String longMessage = "A".repeat(1000);
        FeedBack feedbackPayload = new FeedBack(
                "Test User",
                "test@test.com",
                "Long Feedback",
                longMessage
        );

        given().spec(requestSpec)
                .body(feedbackPayload)
                .when().post("/feedback/submit")
                .then().statusCode(200); //should handle long messages
    }



}
