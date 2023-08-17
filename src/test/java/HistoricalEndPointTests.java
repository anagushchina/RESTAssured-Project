import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class HistoricalEndPointTests {
    @Test
    public void defaultSourceAndCurrencyTest(){
        Response response = given().get(Urls.historicalEndpointAuthorized("2020-05-02"));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("historical", equalTo(true));
        response.then().body("date", equalTo("2020-05-02"));
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes", allOf(hasKey("USDCAD"), hasKey("USDEUR"), hasKey("USDRUB")));
    }

    @ParameterizedTest
    @MethodSource("currency")
    public void specifiedSourceAndCurrencyTest(String date, String source, String currency){
        Response response = given().get(Urls.historicalWithSourceAndCurrency(date, source, currency));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("historical", equalTo(true));
        response.then().body("date", equalTo(date));
        response.then().body("source", equalTo(source));
        response.then().body("quotes", hasKey(source+currency));
        response.then().body("quotes", hasValue(greaterThan(0f)));
    }

    public static Stream<Arguments> currency(){
        return Stream.of(
                Arguments.of("1999-01-01", "CAD", "EUR"),
                Arguments.of("2001-10-05","RUB", "USD"),
                Arguments.of("2015-07-16","EUR", "RUB")
        );
    }

    @Test
    public void sameSourceAndCurrencyTest(){
        String date = "2011-12-31";
        String source = "CAD";
        String currency = "CAD";
        Response response = given().get(Urls.historicalWithSourceAndCurrency(date, source, currency));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("historical", equalTo(true));
        response.then().body("date", equalTo(date));
        response.then().body("source", equalTo(source));
        response.then().body("quotes", empty());
    }

    @ParameterizedTest
    @MethodSource("currencies")
    public void multipleCurrenciesTest(String date, String source, String currency1, String currency2, String currency3){
        Response response = given().get(Urls.historicalWithSourceAndCurrencies(date, source, currency1, currency2, currency3));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("historical", equalTo(true));
        response.then().body("date", equalTo(date));
        response.then().body("source", equalTo(source));
        response.then().body("quotes", allOf(hasKey(source+currency1), hasKey(source+currency2), hasKey(source+currency3)));
        response.then().body("quotes."+source+currency1, greaterThan(0f));
        response.then().body("quotes."+source+currency2, greaterThan(0f));
        response.then().body("quotes."+source+currency3, greaterThan(0f));

    }

    public static Stream<Arguments> currencies(){
        return Stream.of(
                Arguments.of("2018-02-07", "CAD","USD", "EUR", "RUB"),
                Arguments.of("2023-08-10", "RUB","USD", "EUR", "CAD"),
                Arguments.of("2003-11-25", "EUR","USD", "RUB", "CAD")
        );
    }

    @Test
    public void authenticationErrorTest(){
        Response response = given().get(Urls.URL+Urls.HISTORICAL_ENDPOINT+Urls.DATE_TEMPLATE+"&apikey=LRjp0wYtp3UpeZv8");
        System.out.println(response.asString());
        response.then().statusCode(401);
        response.then().body("message", equalTo("Invalid authentication credentials"));
    }

    @Test
    public void noAccessKeyTest(){
        Response response = given().get(Urls.URL+Urls.HISTORICAL_ENDPOINT+Urls.DATE_TEMPLATE);
        System.out.println(response.asString());
        response.then().statusCode(401);
        response.then().body("message", equalTo("No API key found in request"));
    }

    @Test
    public void invalidSourceCodeTest(){
        String date = "2022-04-29";
        String invalidSource = "lpq";
        Response response = given().get(Urls.historicalWithSourceCode(date,invalidSource));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(201));
        response.then().body("error.info", equalTo("You have supplied an invalid Source Currency. [Example: source=EUR]"));
    }

    @Test
    public void invalidCurrencyCodeTest(){
        String date = "2022-04-29";
        String source = "";
        String invalidCurrency = "RU";
        Response response = given().get(Urls.historicalWithSourceAndCurrency(date, source, invalidCurrency));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(202));
        response.then().body("error.info", equalTo("You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]"));
    }

    @Test
    public void noDateTest(){
        Response response = given().get(Urls.URL+Urls.HISTORICAL_ENDPOINT+"?"+Urls.API_KEY);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(301));
        response.then().body("error.info", equalTo("You have not specified a date. [Required format: date=YYYY-MM-DD]"));
    }

    @ParameterizedTest
    @MethodSource("invalidDates")
    public void invalidDateTest(String date){
        String source = "USD";
        String currency = "CAD";
        Response response = given().get(Urls.historicalWithSourceAndCurrency(date, source, currency));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(302));

    }

    public static Stream<Arguments> invalidDates(){
        return Stream.of(
                //Below are a few examples of the most common format mistakes.
                // A complete list of test cases is not provided due to a limit on the number of requests per month.
                Arguments.of("2001/04/05"),
                Arguments.of("2001-13-11"),
                Arguments.of("2022-10-45"),
                Arguments.of("2004-3-30"),
                Arguments.of("2004-03-2"),
                Arguments.of("200-03-02"),
                Arguments.of("2030-03-02")
        );
    }

    @Test
    public void noResultsTest(){
        String source = "USD";
        //The South Sudanese pound was released in July 2018 and was added to Currency Data API since 2023-08-07
        String currency = "SSP";
        String date = "2017-12-12";
        Response response = given().get(Urls.historicalWithSourceAndCurrency(date, source, currency));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(106));
        response.then().body("error.info", equalTo("Your query did not return any results. Please try again."));

    }


    @Test
    public void timeStampTest(){
        String expectedDate = "2022-04-29";
        String source = "EUR";
        String currency = "CAD";
        Response response = given().get(Urls.historicalWithSourceAndCurrency(expectedDate, source, currency));
        System.out.println(response.asString());
        Integer actualMs = response.path("timestamp");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = new Date((long)actualMs*1000);
        String actualDate = format.format(date2.getTime());
        Assertions.assertEquals(expectedDate, actualDate);
    }

}
