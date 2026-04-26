package tests;

import io.qameta.allure.Description;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
@Epic("Управление пользователями")
@Feature("Регистрация")
@Tag("Api")
@Owner("QA")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private static final String baseUrl = "https://playground.learnqa.ru/api_dev/";
    @Test
    @Story("Создание пользователя с существующим email")
    public void testCreateUserWithExistingEmail(){
        String email ="vinkotov@examle.com";
        Map<String,String> userData = new HashMap<>();
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured.given()
                .body(userData)
                .post(baseUrl + "user/")
                .andReturn();
        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");

    }
    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Успешное создание пользователя")
    public void testCreateUserSuccesfully(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = RestAssured.given()
                .body(userData)
                .post(baseUrl + "user/")
                .andReturn();
        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }
    //Ex15: Тесты на метод user
    @Description("Negative test: checks if it’s possible to create a user without an '@' in the email address")
    @DisplayName("Test User Creation without '@' in the email")
    @Story("Создание пользователя без @ в почте")
    @Test
    public void testCreateUserWithIncorrectEmail(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.put("email", userData.get("email").replace("@", ""));
        Response responseCreateAuth = apiCoreRequests.makePostRequest(baseUrl + "user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Invalid email format");
    }

    @Description("Negative test: checks if it’s possible to create a user with short name")
    @DisplayName("Test User Creation with short name")
    @Story("Создание пользователя с коротким именем")
    @Test
    public void testCreateUserWithShortUserName(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.put("username", DataGenerator.getUserNameWithLenght(1));
        Response responseCreateAuth = apiCoreRequests.makePostRequest(baseUrl + "user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too short");
    }

    @Description("Negative test: checks if it’s possible to create a user with long name")
    @DisplayName("Test User Creation with long name")
    @Story("Создание пользователя с длинным именем")
    @Test
    public void testCreateUserWithLongUserName(){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.put("username", DataGenerator.getUserNameWithLenght(255));
        Response responseCreateAuth = apiCoreRequests.makePostRequest(baseUrl + "user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too long");
    }

    static Stream<String> removeField() {
        return Stream.of("email", "password", "username", "firstName", "lastName");
    }
    @ParameterizedTest
    @MethodSource("removeField")
    @Description("Negative test: verifies that user cannot be created when one of required parameters is missing")
    @Story("Создание пользователя без одного из обязательных параметров")
    public void TestCreateUserWithDeleteField(String removeField){
        Map<String,String> userData = DataGenerator.getRegistrationData();
        userData.remove(removeField);
        Response responseCreateAuth = apiCoreRequests.makePostRequest(baseUrl + "user/",userData);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The following required params are missed: " + removeField);
    }


}
