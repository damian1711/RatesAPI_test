package rest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import org.junit.Assert;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.Map;


import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;


public class APIvalidator {

    static boolean isBaseProvided;
    static boolean areRatesCcyProvided;
    static int numberOfRatesCcy = 0;
    public static Response response;
    public static JsonPath jsonPathEvaluator;
    public static ValidatableResponse Vresponse;


    public static void verifyIfBaseSyntaxIsCorrect(String baseCcy) { // this method is mainly to protect from running positive test with invalid params (baseCcy).
        Assert.assertTrue("Wrong base currency syntax. Base must be provided as 3 upper case letters e.g. USD. " +
                "Only one base currency can be provided at the time", Pattern.matches("^[A-Z]{3}$", baseCcy));
    }

    public static void verifyIfCurrenciesSyntaxIsCorrect(String currencies) { // this method is mainly to protect from running positive test with invalid params (currencies).
        for (String currency : currencies.split(",")) {
            Assert.assertTrue("Wrong rates currencies syntax. " +
                    "Currency must be provided as 3 upper case letters e.g. USD. " +
                    "If you want to check more than one currency then use comma e.g. USD,GBP,EUR", Pattern.matches("^[A-Z]{3}$", currency));
            numberOfRatesCcy += numberOfRatesCcy;
        }
    }

    public static String getErrorMessageForFailedRequest(Response response) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        return jsonPathEvaluator.get().toString();
    }

    public static void checkStatusCode(Response response, boolean isItNegativeTest) {

        String responseCode = response.getStatusLine();
        //negative cases if server is not returning results
        Assert.assertNotEquals("Server is busy or not available, try again later", "HTTP/1.1 408 Request Timeout", responseCode);
        Assert.assertNotEquals("Server is not available, try again later", "HTTP/1.1 404 Not Found", responseCode);
        Assert.assertNotEquals("The requested resource is unavailable at this present time", "HTTP/1.1 403 Forbidden", responseCode);
        Assert.assertNotEquals("The user is unauthorized to access the requested resource.", "HTTP/1.1 401 Unauthorized", responseCode);
        if (responseCode.equals("HTTP/1.1 400 Bad Request")) // for 400 we want to capture error and print it rather than fail assert at start
        {
            System.out.println("Error message is: " + getErrorMessageForFailedRequest(response));  // we expect that all negative tests goes here and return error message
            if (!isItNegativeTest)
                Assert.assertEquals("The request cannot be fulfilled due to bad syntax.", "HTTP/1.1 200 OK", responseCode);
        } else if (isItNegativeTest)
            System.out.println("Error message is: " + getErrorMessageForFailedRequest(response));  // we expect that all negative tests goes here and return error message

        //expected result for correct syntax query
        if (!isItNegativeTest)
            Assert.assertEquals("API is not available OR wrong URL provided", "HTTP/1.1 200 OK", responseCode);
    }

    public static void runGetRequestWithParams(String currencies, String baseCcy) {
        RequestSpecification httpRequest = given();                                                     // prepare request
        response = httpRequest.get("?base=" + baseCcy + "&symbols=" + currencies);                  //build query with tested params
        QueryableRequestSpecification queryable = SpecificationQuerier.query(httpRequest);          // get queryable object to validate our request details
        System.out.println("Header is: " + queryable.getURI());                                     //print URI that we test in given scenario
        jsonPathEvaluator = response.jsonPath();
        Vresponse = when().get("https://api.ratesapi.io/api/latest?base=" + baseCcy + "&symbols=" + currencies).then().header("Connection", "keep-alive");

    }

    public static void runGetRequestNoParams() {
        RequestSpecification httpRequest = given();                                                     // prepare request
        response = httpRequest.get();                  //build query with tested params
        QueryableRequestSpecification queryable = SpecificationQuerier.query(httpRequest);          // get queryable object to validate our request details
        System.out.println("Header is: " + queryable.getURI());                                     //print URI that we test in given scenario
        jsonPathEvaluator = response.jsonPath();
    }


    private static void ValidateBaseCcyResponse(String inputBaseCcy) {
        String actualBaseCcy = jsonPathEvaluator.get("base");
        if (isBaseProvided)
            Assert.assertEquals("Wrong base currency in response", inputBaseCcy, actualBaseCcy);
        else
            Assert.assertEquals("Default base currency should be EUR", "EUR", actualBaseCcy);
    }


    public static void validateCCyBody(String currencies) {
        for (String currency : currencies.split(",")) {         // split tested currencies with ,
            Vresponse.body("rates." + currency, notNullValue());   //verify if response body contain currency that was in request(String currencies)
            Vresponse.body("rates." + currency, not(equalTo(0)));  //verify that result of tested API return all currencies rates as not 0 - we expect any non zero rate


            Map<String, Double> rates = Vresponse.extract().response().path("rates");
            for (String currencyKey : rates.keySet()) {         //for each currency returned from API get
                Assert.assertTrue(Pattern.matches("^[A-Z]{3}$", currencyKey));  //ensure it match 3 upper case letters
            }
        }
    }

    public static void verifyIfCorrectDateIsUsed(String specificDate) {
        String actualGetTime = jsonPathEvaluator.get("date");
        if (specificDate == null) // when no date provided it will be default (current date)- being either today or yday (market close)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);

            if (actualGetTime.equals(String.valueOf(LocalDate.now())) || actualGetTime.equals(dateFormat.format(cal.getTime()))) {
                System.out.println("Date not provided so default (current) date used: " + actualGetTime);
            } else
                Assert.assertEquals("Wrong date returned in response", java.time.LocalDate.now(), actualGetTime);

        } else  // validate if this is future date test - if yes return actual date that will be used
        {
            LocalDate input = LocalDate.parse(specificDate);
            LocalDate output = LocalDate.parse(actualGetTime);
            boolean isFutureDateTest = input.isAfter(LocalDate.now());
            if (isFutureDateTest) //   validate if this is future date test - if yes return actual date that will be used
                System.out.println("Input date: " + input + " is in future hence converting to latest (current) date: " + output);
            else            // if input date<>output fail assert and return error message
                Assert.assertEquals("Wrong date returned in response", specificDate, actualGetTime);
        }
    }

    public static void GetandAssertResponse(String currencies, String baseCcy, String SpecificDate, boolean isItNegativeTest) {
        if (baseCcy.length() > 0 && !isItNegativeTest) {  //check Syntax only if baseCcy was provided.
            verifyIfBaseSyntaxIsCorrect(baseCcy);
            isBaseProvided = true;
        } else {
            System.out.println("Base currency was not provided so default currency will be used as a base: EUR");
            isBaseProvided = false;
        }

        if (currencies.length() > 0 && !isItNegativeTest) { //check Syntax only if currencies filter were provided (otherwise it will return all currencies for given base.
            verifyIfCurrenciesSyntaxIsCorrect(currencies);
            areRatesCcyProvided = true;
        } else {
            System.out.println("No filter was applied on Rates hence all currencies rate for base currency will be returned");
            areRatesCcyProvided = false;
        }
        runGetRequestWithParams(currencies, baseCcy);
        checkStatusCode(response, isItNegativeTest);  // this test will proof if negative tests indeed failed and have error message

        if (!isItNegativeTest) // run only for positive tests - validation of response body for Rates and Ccy
        {
            ValidateBaseCcyResponse(baseCcy);
            validateCCyBody(currencies);
            String responseBody = response.getBody().asString();
            System.out.println("Response Body is =>  " + responseBody);
            verifyIfCorrectDateIsUsed(SpecificDate);
        }
        String jsonResponse = response.jsonPath().getString("date");
    }

    public static void GetandAssertResponseWrongURL(boolean isItNegativeTest) {
        runGetRequestNoParams();
        checkStatusCode(response, isItNegativeTest);
    }
}
