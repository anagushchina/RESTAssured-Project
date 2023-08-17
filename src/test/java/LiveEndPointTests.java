import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Stream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class LiveEndPointTests {

    @Test
    public void defaultSourceAndCurrencyTest(){
        Response response = given().get(Urls.liveEndpointAuthorized());
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes", allOf(hasKey("USDCAD"), hasKey("USDEUR"), hasKey("USDRUB")));
    }

    @ParameterizedTest
    @MethodSource("currency")
    public void specifiedSourceAndCurrencyTest(String source, String currency){
        Response response = given().get(Urls.liveWithSourceAndCurrency(source, currency));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("source", equalTo(source));
        response.then().body("quotes", hasKey(source+currency));
        response.then().body("quotes", hasValue(greaterThan(0f)));
    }

    public static Stream<Arguments> currency(){
        return Stream.of(
                Arguments.of("CAD", "EUR"),
                Arguments.of("RUB", "USD"),
                Arguments.of("EUR", "RUB")
        );
    }

    @Test
    public void sameSourceAndCurrencyTest(){
        String source = "CAD";
        String currency = "CAD";
        Response response = given().get(Urls.liveWithSourceAndCurrency(source, currency));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("source", equalTo(source));
        response.then().body("quotes", empty());
    }

    @ParameterizedTest
    @MethodSource("currencies")
    public void multipleCurrenciesTest(String source, String currency1, String currency2, String currency3){
        Response response = given().get(Urls.liveWithSourceAndCurrencies(source, currency1, currency2, currency3));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("source", equalTo(source));
        response.then().body("quotes", allOf(hasKey(source+currency1), hasKey(source+currency2), hasKey(source+currency3)));
        response.then().body("quotes."+source+currency1, greaterThan(0f));
        response.then().body("quotes."+source+currency2, greaterThan(0f));
        response.then().body("quotes."+source+currency3, greaterThan(0f));

    }

    public static Stream<Arguments> currencies(){
        return Stream.of(
                Arguments.of("CAD","USD", "EUR", "RUB"),
                Arguments.of("RUB","USD", "EUR", "CAD"),
                Arguments.of("EUR","USD", "RUB", "CAD")
        );
    }

    @Test
    public void authenticationErrorTest(){
        Response response = given().get(Urls.URL+Urls.LIVE_ENDPOINT+"?apikey=LRjp0wYtp3UpeZv8");
        System.out.println(response.asString());
        response.then().statusCode(401);
        response.then().body("message", equalTo("Invalid authentication credentials"));
    }

    @Test
    public void noAccessKeyTest(){
        Response response = given().get(Urls.URL+Urls.LIVE_ENDPOINT);
        System.out.println(response.asString());
        response.then().statusCode(401);
        response.then().body("message", equalTo("No API key found in request"));
    }

    @Test
    public void invalidSourceCodeTest(){
        Response response = given().get(Urls.liveWithSourceCode("fzx"));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(201));
        response.then().body("error.info", equalTo("You have supplied an invalid Source Currency. [Example: source=EUR]"));
    }

    @Test
    public void invalidCurrencyCodeTest(){
        Response response = given().get(Urls.liveWithSourceAndCurrency("", "RU"));
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(202));
        response.then().body("error.info", equalTo("You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]"));
    }

    @Test
    public void timeStampTest(){
        Response   response = given().get(Urls.liveWithSourceAndCurrency("EUR", "CAD"));
        System.out.println(response.asString());
        //get yesterday date as String
        String expected = LocalDate.now().toString();

        //get timestamp from response
        Integer actualMs = response.path("timestamp");

        //create format to match expected String date
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //get Date from the timestamp in request - a hard part, we need to set it to long and multiply by 1000
        //as in this case API returns UNIX time and not epoch time
        Date date2 = new Date((long)actualMs*1000);

        //format date from response to match expected String date
        String actual = format.format(date2.getTime());

        //compare
        Assertions.assertEquals(expected, actual);
    }


}
