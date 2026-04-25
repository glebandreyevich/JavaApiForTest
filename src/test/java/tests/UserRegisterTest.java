package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testCreateUserWithExistingEmail(){
        String email ="vinkotov@examle.com";
        Map<String,String> userData = new HashMap<>();
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured.given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();
        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");

    }
    @Test
    public void testCreateUserSuccesfully(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = RestAssured.given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();
        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
        responseCreateAuth.prettyPrint();
    }
    //Ex15: Тесты на метод user
    @Description("Negative test: checks if it’s possible to create a user without an '@' in the email address")
    @DisplayName("Test User Creation without '@' in the email")
    @Test
    public void testCreateUserWithIncorrectEmail(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.put("email", userData.get("email").replace("@", ""));
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Invalid email format");
    }

    @Description("Negative test: checks if it’s possible to create a user with short name")
    @DisplayName("Test User Creation with short name")
    @Test
    public void testCreateUserWithShortUserName(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.put("username", DataGenerator.getUserNameWithLenght(1));
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too short");
    }

    @Description("Negative test: checks if it’s possible to create a user with long name")
    @DisplayName("Test User Creation with long name")
    @Test
    public void testCreateUserWithLongUserName(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.put("username", DataGenerator.getUserNameWithLenght(255));
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too long");
    }

    static Stream<String> removeField() {
        return Stream.of("email", "password", "username", "firstName", "lastName");
    }
    @ParameterizedTest
    @MethodSource("removeField")
    @Description("Negative test: verifies that user cannot be created when one of required parameters is missing")
    @DisplayName("Test User Creation without required parameters")
    public void TestCreateUserWithDeleteField(String removeField){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.remove(removeField);
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The following required params are missed: " + removeField);
    }


}
