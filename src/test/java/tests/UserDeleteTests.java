package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTests extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("Negative test: checks if it’s possible to delete a user with admin rights")
    @DisplayName("Admin user delete test")
    @Test
    public void DeleteAdminUserTest(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        String userId = responseGetAuth.jsonPath().getString("user_id");
        Response DeleteResponse = apiCoreRequests.makeDeleteRequestWithTokenAndAuthCookie("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertJsonByName(DeleteResponse,"error","Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }
    @Description("Positive test: Create user, authenticate, delete user, then verify user is no longer accessible")
    @DisplayName("Delete user and verify deletion")
    @Test
    public void DeleteUser(){
        Map<String, String> newUser = DataGenerator.getRegistrationData();
        Response responseGetUserAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", newUser);
        Map<String, String> authData = new HashMap<>();
        authData.put("email", newUser.get("email"));
        authData.put("password", newUser.get("password"));
        String userId = responseGetUserAuth.jsonPath().getString("id");
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        Response ResponseDelete = apiCoreRequests.makeDeleteRequestWithTokenAndAuthCookie("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Response responseGetUser = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/"+ userId,
                this.getCookie(responseGetAuth, "auth_sid")
                ,this.getHeader(responseGetAuth, "x-csrf-token"));
        Assertions.assertResponseTextEquals(responseGetUser,"User not found");
        Assertions.assertJsonByName(ResponseDelete,"success","!");
    }
    @Description("Negative test: Try to delete a user while being authenticated as another user")
    @DisplayName("Delete user as different user")
    @Test
    public void DeleteUserCheck(){
        Map<String, String> newUserForDelete = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", newUserForDelete);
        String userId = responseCreateUser.jsonPath().getString("id");

        Map<String, String> newUserForAuth = DataGenerator.getRegistrationData();
        apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", newUserForAuth);
        Map<String, String> authData = new HashMap<>();
        authData.put("email", newUserForAuth.get("email"));
        authData.put("password", newUserForAuth.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        Response ResponseDelete = apiCoreRequests.makeDeleteRequestWithTokenAndAuthCookie("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertJsonByName(ResponseDelete,"error","This user can only delete their own account.");
    }


}
