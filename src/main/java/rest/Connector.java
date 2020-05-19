package rest;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import io.restassured.RestAssured;

public class Connector {

    final String BaseURL = "https://api.ratesapi.io/api/";
    public String defaultURL = BaseURL;

    public static Response callAPIforGivenURL() {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.request(Method.GET);
        return response;
    }

    public static void isAPIavailable() {
        String ResponseFormatType = callAPIforGivenURL().getContentType();
        Assert.assertEquals("API does not return JSON", "application/json", ResponseFormatType);
    }

    public static void isAPIreturningSuccesResponse() { ;
        String statusCode = callAPIforGivenURL().getStatusLine();
        Assert.assertEquals("API is not available OR wrong URL provided", "HTTP/1.1 200 OK", statusCode);
    }

}

