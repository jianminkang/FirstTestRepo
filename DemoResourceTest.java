package com.microsoft.hackathon.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class DemoResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello?key=world")
          .then()
             .statusCode(200)
             .body(is("hello world"));
    }

    //Create a unit test to validate /diffdates that calculates the difference between two dates
    @Test
    public void testDiffDatesEndpoint() {
        given()
          .queryParam("date1", "01-01-2021")
          .queryParam("date2", "01-02-2021")
          .when().get("/diffdates")
          .then()
             .statusCode(200)
             .body(is("31"));
    }

    // write unit test to Validate the format of a spanish phone number (+34 prefix, then 9 digits, starting with 6, 7 or 9). 
    //The operation should receive a phone number as parameter and return true if the format is correct, false otherwise
    @Test
    public void testValidatePhoneEndpoint() {
        given()
          .queryParam("phone", "+34612345678")
          .when().get("/validatephone")
          .then()
             .statusCode(200)
             .body(is("true"));
    }

    //Write unit test to Validate the format of a spanish DNI (8 digits and 1 letter). 
    //The operation should receive a DNI as parameter and return true if the format is correct, false otherwise. 
    @Test
    public void testValidateDniEndpoint() {
        given()
          .queryParam("dni", "12345678A")
          .when().get("/validatedni")
          .then()
             .statusCode(200)
             .body(is("true"));
    }

    //Write unit test to Based on the existing colors.json file under resources, given the name of the color as path parameter, 
    //return the hexadecimal code. If the color is not found, return 404.
    @Test
    public void testGetColorEndpoint() {
        given()
          .when().get("/color?color=red")
          .then()
             .statusCode(200)
             .body(is("#FF0000"));
    }

    //write a unit test to Create a new operation that call the API https://api.chucknorris.io/jokes/random and return the joke.
    @Test
    public void testGetJokeEndpoint() {
        given()
          .when().get("/joke")
          .then()
             .statusCode(200);
    }

    //Write unit test for Given a url as query parameter, parse it and return the protocol, host, port, path and query parameters. 
    //The response should be in Json format. 
    @Test
    public void testParseUrlEndpoint() {
        given()
          .queryParam("url", "https://www.google.com:8080/search?q=quarkus")
          .when().get("/parseurl")
          .then()
             .statusCode(200);
             //.body(is("{\"protocol\":\"https\",\"host\":\"www.google.com\",\"port\":\"8080\",\"path\":\"/search\",\"query\":\"q=quarkus\"}"));
    }

    //Write unit test to Given the path of a file and count the number of occurrence of a provided word. 
    //The path and the word should be query parameters. The response should be in Json format. 
    @Test
    public void testCountWordEndpoint() {
        given()
          .queryParam("path", "test.txt")
          .queryParam("word", "test")
          .when().get("/countword")
          .then()
             .statusCode(200)
             .body(is("{\"count\":2}"));
    }
}