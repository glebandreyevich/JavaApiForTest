import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ApiTest {

    //Парсинг Json
    @Test
    public void JsonParsing() {
        String url = "https://playground.learnqa.ru/api/get_json_homework";
        JsonPath jsonPath = RestAssured
                .get(url)
                .jsonPath();
        String secondMessage = jsonPath.getString("messages[1].message");
        System.out.println("Second message: " + secondMessage);
    }

    //Редирект
    @Test
    public void Redirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(url);
        String location = response.getHeader("Location");
        System.out.println("Redirect " + location);
    }

    //Долгий редирект
    @Test
    public void RecursRedirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        while (true) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url);
            int statusCode = response.getStatusCode();
            if (statusCode == 200) {
                System.out.println("OK:" + statusCode);
                break;
            } else if (statusCode == 301) {
                url = response.getHeader("Location");
                System.out.println(url);
            }
        }
    }

    //Токены
    @Test
    public void TokenTest() throws InterruptedException {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        Response tokenresponse = RestAssured
                .get(url);
        String token = tokenresponse.jsonPath().getString("token");
        int waitSecond = tokenresponse.jsonPath().getInt("seconds");
        Response responseBefore = RestAssured
                .given()
                .queryParam("token", token)
                .get(url);
        String statusBefore = responseBefore.jsonPath().getString("status");
        System.out.println(statusBefore);
        assertNotEquals("Job is ready", statusBefore);
        Thread.sleep(waitSecond * 1000L);
        Response responseAfter = RestAssured
                .given()
                .queryParam("token", token)
                .get(url);
        String statusAfter = responseAfter.jsonPath().getString("status");
        String resultAfter = responseAfter.jsonPath().getString("result");
        System.out.println(statusAfter);
        System.out.println(resultAfter);
        assertEquals("Job is ready", statusAfter);
        assertEquals("42", resultAfter);
    }

    //Подбор пароля
    @Test
    public void passwordBrute() {
        JsonPath jsonPath = RestAssured
                .given()
                .header("User-Agent", "Test")
                .queryParam("table", "1")
                .when()
                .get("https://www.wikitable2json.com/api/List_of_the_most_common_passwords").jsonPath();

        List<List<String>> table = jsonPath.getList("[0]");
        Set<String> passwords = new HashSet<>();

        for (int i = 1; i < table.size(); i++) {
            List<String> row = table.get(i);
            for (int j = 1; j < row.size(); j++) {
                String password = row.get(j);
                password = password.replaceAll("[^a-zA-Z0-9]", "");
                passwords.add(password);
            }
        }

        for (String password : passwords) {
            Response responsePass = RestAssured
                    .given()
                    .queryParam("login", "super_admin")
                    .queryParam("password", password)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/get_secret_password_homework");
            String authCookie = responsePass.getCookie("auth_cookie");
            Response responseCookie = RestAssured
                    .given()
                    .cookie("auth_cookie", authCookie)
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie");
            String status = (responseCookie.getBody().asString());
            if (status.equals("You are authorized")) {
                System.out.println("Ваш пароль: " + password);
                break;
            }
        }
    }

}
