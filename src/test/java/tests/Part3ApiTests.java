package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class Part3ApiTests extends BaseTestCase {
    //Короткая фраза
    @Test
    public void ShortPhraseTest() {
        Random random = new Random();
        int lenghtText = random.nextInt(5) + 14;
        String randomLenghtText = new String(new char[lenghtText]).replace("\0", "a");
        assertTrue(randomLenghtText.length() > 15, "Expected Text length > 15, but it was only: " + randomLenghtText.length());
    }
    //Cookie
    @Test
    public void CookieTest() {
        Response responseCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie");
        assertEquals("hw_value", getCookie(responseCookie, "HomeWork"), "Cookie has wrong value");
    }
    //Header
    @Test
    public void HeaderTest() {
        Response responseHeader = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header");
        assertEquals("Some secret value",
                getHeader(responseHeader, "x-secret-homework-header"),
                "Header 'x-secret-homework-header' has wrong value");
    }
    //User Agent
    public record ExpectedResult(String platform, String browser, String device) {
    }

    static Stream<Arguments> userAgentDataProvider() {
        return Stream.of(Arguments.of("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
                        , new ExpectedResult("Mobile", "No", "Android"))
                , Arguments.of("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1"
                        , new ExpectedResult("Mobile", "Chrome", "iOS"))
                , Arguments.of("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html"
                        , new ExpectedResult("Googlebot", "Unknown", "Unknown"))
                , Arguments.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0"
                        , new ExpectedResult("Web", "Chrome", "No"))
                , Arguments.of("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
                        , new ExpectedResult("Mobile", "No", "iPhone"))
        );

    }

    @ParameterizedTest
    @MethodSource("userAgentDataProvider")
    public void UserAgentTest(String userAgent, ExpectedResult ExpectedResult) {
        Response response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check");
        assertAll("For User-Agent " + userAgent + " expected result does not match actual result" ,
                () -> assertEquals(ExpectedResult.platform, getStringFromJson(response, "platform")),
                () -> assertEquals(ExpectedResult.browser, getStringFromJson(response, "browser")),
                () -> assertEquals(ExpectedResult.device, getStringFromJson(response, "device"))
        );
    }
}
