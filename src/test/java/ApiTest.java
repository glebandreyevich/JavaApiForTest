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


}
