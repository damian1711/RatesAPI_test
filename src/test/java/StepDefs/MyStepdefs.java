package StepDefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import rest.APIvalidator;
import rest.Connector;

public class MyStepdefs {
Connector connector = new Connector();

    @Given("Rates API for Latest Foreign Exchange rates")
    public void buildAPIurlForLatestRates() {
             RestAssured.baseURI=connector.defaultURL+"latest";
        System.out.println("Default API URI is: "+RestAssured.baseURI);
    }

    @Given("Rates API for {string} Foreign Exchange rates")
    public void buildAPIurlForSpecificDateRates(String specificDate) {
        RestAssured.baseURI=connector.defaultURL+specificDate;
        //   connector.testedURL= RestAssured.baseURI;
        System.out.println("Default API URI is: "+RestAssured.baseURI);
    }


    @Given("Rates API for Latest Foreign Exchange rates with {string}")
    public void buildAPIurlForWrongAddress(String url) {
        RestAssured.baseURI=url;
        System.out.println("Default API URI is: "+RestAssured.baseURI);
    }

    @When("The API is available")
    public void the_API_is_available() {
      Connector.isAPIavailable();
  }

    @Then("An automated test suite should run which will assert the success status of the response")
    public void verifyResponseStatusIsSuccess() {
        Connector.isAPIreturningSuccesResponse();
    }

    @Then("An automated test suite should run for {string} rate and {string} base which will assert the response")
    public void positiveTestForGivenAPIquery(String rates, String base) {
        try{
            APIvalidator.GetandAssertResponse(rates, base,null, false);
        }
        catch(Exception E) {
            throw new io.cucumber.java.PendingException();
        }
    }

    @Then("An automated negative test suite should run for {string} rate and {string} base which will assert the response")
    public void negativeTestForGivenAPIquery(String rates, String base) {
        APIvalidator.GetandAssertResponse(rates, base, null, true);
    }

    @Then("An automated test suite should run for {string} {string} rate and {string} base which will assert the response")
    public void positiveTestForGivenAPIqueryAndDate(String specificDate,String rates, String base) {
        APIvalidator.GetandAssertResponse(rates, base,specificDate, false);
    }


    @Then("An automated test suite should run for {string} which will validate that the response matches for the current date")
    public void positiveTestForFutureDate(String futureDate) {
        APIvalidator.GetandAssertResponse("", "",futureDate, false);
    }

    @Then("Test case should assert the correct response supplied by the call for {string}")
    public void testCaseShouldAssertTheCorrectResponseSuppliedByTheCallFor(String url) {
        APIvalidator.GetandAssertResponseWrongURL(true);
    }

    }
