import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class InvalidPathTests {

    @Test
    public void invalidEndPointTest(){
        Response response = given().get(Urls.URL+"/livee"+"?"+Urls.API_KEY);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(103));
        response.then().body("error.info", equalTo("This API Function does not exist."));
    }

    @Test
    public void invalidURLTest(){
        Response response = given().get("https://api.apilayer.com/wrong_path");
        System.out.println(response.asString());
        response.then().statusCode(404);
        response.then().body("message", equalTo("no Route matched with those values"));
    }

}
