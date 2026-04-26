package tests;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Управление пользователями")
@Feature("Получение данных")
@Story("Получение данных пользователя")
@Tag("Api")
@Owner("QA")
public class UserGetTest extends BaseTestCase{
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private static final String baseUrl = "https://playground.learnqa.ru/api_dev/";
    @Test
    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get(baseUrl + "user/2")
                .andReturn();
        Assertions.assertJsonHasNotField(responseUserData,"Username");
        Assertions.assertJsonHasNotField(responseUserData,"firstName");
        Assertions.assertJsonHasNotField(responseUserData,"lastName");
        Assertions.assertJsonHasNotField(responseUserData,"email");

    }
    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post(baseUrl + "user/login/")
                .andReturn();
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
                Response responseUserData = RestAssured
                        .given()
                        .header("x-csrf-token",header)
                        .cookie("auth_sid",cookie)
                        .get(baseUrl + "user/2")
                        .andReturn();
                String[] expectedFields =  {"username","firstName","lastName","email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);
    }
    //Ex16: Запрос данных другого пользователя
    @Description("Negative test: verifies that authenticated user cannot see full data of another user - only username is returned")
    @DisplayName("Get another user data while authenticated only username visible")
    @Attachment("")
    @Test
    public void RequestForAnotherUserTest(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseGetAuth = apiCoreRequests.makePostRequest(baseUrl + "user/login/",authData);
        Response responseGetUserId = apiCoreRequests.makeGetRequest(baseUrl + "user/159042",
                this.getCookie(responseGetAuth, "auth_sid")
                ,this.getHeader(responseGetAuth, "x-csrf-token"));
        Assertions.assertJsonHasField(responseGetUserId, "username");
        Assertions.assertJsonHasNotField(responseGetUserId,"user_id");
    }
}
