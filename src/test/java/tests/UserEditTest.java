package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
@Epic("Управление пользователями")
@Feature("Редактирование")
@Story("Редактирование пользователя")
@Tag("Api")
@Owner("QA")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private static final String baseUrl = "https://playground.learnqa.ru/api_dev/";
    

    @Test
    public void testEditJustCreated() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(baseUrl + "user/")
                .jsonPath();
        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post(baseUrl + "user/login/")
                .andReturn();

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put(baseUrl + "user/" + userId)
                .andReturn();

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get(baseUrl + "user/" + userId)
                .andReturn();
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
    //Ex17: Негативные тесты на PUT
    @Description("Negative test: verifies that user cannot update user data without authentication")
    @DisplayName("Update user data while unauthorized  error expected")
    @Test
    public void TestForUnauthorizedEditUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(baseUrl + "user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");
        Response responseEditUser = apiCoreRequests.makePutRequest(baseUrl + "user/" + userId, userData);
        Assertions.assertJsonByName(responseEditUser,"error","Auth token not supplied");
    }
    @Description("Negative test: verifies that authenticated user cannot update another user's data")
    @DisplayName("Update user as a different user")
    @Test
    public void TestForEditUser() {
        Map<String, String> newUser = DataGenerator.getRegistrationData();
        apiCoreRequests.makePostRequest(baseUrl + "user/", newUser);
        Map<String, String> authData = new HashMap<>();
        authData.put("email", newUser.get("email"));
        authData.put("password", newUser.get("password"));
        Map<String, String> targetUser = DataGenerator.getRegistrationData();
        Response responseCreateUserForEdit = apiCoreRequests.makePostRequest(baseUrl + "user/", targetUser);
        String userId = responseCreateUserForEdit.jsonPath().getString("id");
        Response AuthResponse = apiCoreRequests.makePostRequest(baseUrl + "user/login/", authData);
        Map<String, String> newDataForEdit = DataGenerator.getRegistrationData();
        Response EditResponse = apiCoreRequests.makePutRequestWithTokenCookie(baseUrl + "user/" + userId, newDataForEdit,
                this.getHeader(AuthResponse, "x-csrf-token"),
                this.getCookie(AuthResponse, "auth_sid"));
        Assertions.assertJsonByName(EditResponse,"error","This user can only edit their own data.");
    }
    @Description("Negative test: verifies that user cannot update email address with invalid format (missing '@') even when authenticated")
    @DisplayName("Update own email to address without '@'")
    @Test
    public void TestForEditUserEmail() {
        Map<String, String> newUser = DataGenerator.getRegistrationData();
        Response responseGetUserAuth = apiCoreRequests.makePostRequest(baseUrl + "user/", newUser);
        Map<String, String> authData = new HashMap<>();
        authData.put("email", newUser.get("email"));
        authData.put("password", newUser.get("password"));
        String userId = responseGetUserAuth.jsonPath().getString("id");
        Response AuthResponse = apiCoreRequests.makePostRequest(baseUrl + "user/login/", authData);
        Map<String, String> newDataForEdit = new HashMap<>();
        newDataForEdit.put("email", authData.get("email").replace("@", ""));
        Response EditResponse = apiCoreRequests.makePutRequestWithTokenCookie(baseUrl + "user/" + userId, newDataForEdit,
                this.getHeader(AuthResponse, "x-csrf-token"),
                this.getCookie(AuthResponse, "auth_sid"));
        Assertions.assertJsonByName(EditResponse,"error","Invalid email format");
    }
    @Description("Negative test: verifies that user cannot update firstName with value that is too short even when authenticated")
    @DisplayName("Update own firstName to single character")
    @Test
    public void TestForEditUserFirstName() {
        Map<String, String> newUser = DataGenerator.getRegistrationData();
        Response responseGetUserAuth = apiCoreRequests.makePostRequest(baseUrl + "user/", newUser);
        Map<String, String> authData = new HashMap<>();
        authData.put("email", newUser.get("email"));
        authData.put("password", newUser.get("password"));
        String userId = responseGetUserAuth.jsonPath().getString("id");
        Response AuthResponse = apiCoreRequests.makePostRequest(baseUrl + "user/login/", authData);
        Map<String, String> newDataForEdit = new HashMap<>();
        newDataForEdit.put("firstName", newUser.get("firstName").replace("learnqa", "l"));
        Response EditResponse = apiCoreRequests.makePutRequestWithTokenCookie(baseUrl + "user/" + userId, newDataForEdit,
                this.getHeader(AuthResponse, "x-csrf-token"),
                this.getCookie(AuthResponse, "auth_sid"));
        Assertions.assertJsonByName(EditResponse,"error","The value for field `firstName` is too short");
    }
}
