import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class HelloWorld {

    @Test
    public void ApiGetTest() {
        Map <String, String> params = new HashMap<>();
        params.put("name","John");
        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
                String answer = response.get("answer2");
                if (answer==null){
                    System.out.println("The key 'answer' is absent");
                }
                else {
                    System.out.println(answer);
                }
    }
    @Test
    public void ApiCheckType() {
        Map<String, String> data = new HashMap<>();
        data.put("login","secret_login3");
        data.put("password","secret_pass3");
        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();
        String responseCookie =responseForGet.getCookie("auth_cookie");
        Map < String, String> cookies = new HashMap<>();
        if (responseCookie!=null)
        {
            cookies.put("auth_cookie",responseCookie);
        }
        cookies.put("auth_cookie",responseCookie);
        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();
        responseForCheck.print();

    }

}
