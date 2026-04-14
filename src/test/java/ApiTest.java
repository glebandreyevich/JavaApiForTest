import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;


import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    public void Redirect()
    {
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
    public void RecursRedirect()
    {
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
            }
            else if (statusCode==301) {
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
                .queryParam("token",token)
                .get(url);
        String statusBefore = responseBefore.jsonPath().getString("status");
        System.out.println(statusBefore);
        assertNotEquals("Job is ready",statusBefore);
        Thread.sleep(waitSecond * 1000L);
        Response responseAfter = RestAssured
                .given()
                .queryParam("token",token)
                .get(url);
        String statusAfter = responseAfter.jsonPath().getString("status");
        String resultAfter = responseAfter.jsonPath().getString("result");
        System.out.println(statusAfter);
        System.out.println(resultAfter);
        assertEquals ("Job is ready", statusAfter);
        assertEquals("42",resultAfter);
    }


}
