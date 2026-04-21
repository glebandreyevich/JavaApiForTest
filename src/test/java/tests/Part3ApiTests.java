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
}
