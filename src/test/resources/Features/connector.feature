@Test
Feature: Connector

  Scenario: 1 - Check if Rates API is available
    Given Rates API for Latest Foreign Exchange rates
    When The API is available
    Then An automated test suite should run which will assert the success status of the response


  Scenario Outline: 2 - Check Rates API response for "<rates>" rate and "<base>"
    Given Rates API for Latest Foreign Exchange rates
    When The API is available
    Then An automated test suite should run for "<rates>" rate and "<base>" base which will assert the response
    Examples:
      | rates                                       | base |
      | PLN                                         | EUR  |
      | GBP                                         | PLN  |
      | EUR,GBP                                     | PLN  |
      | HKD,CHF,CZK,CNY,PLN,CAD,NZD,JPY,RUB,USD,AUD | EUR  |
      |                                             |      |
      | EUR                                         |      |
      | USD                                         |      |
      |                                             | PLN  |

  Scenario Outline: 3 - Check Rates API response correctness for "<rates>" rate and "<base>"
    Given Rates API for Latest Foreign Exchange rates
    When The API is available
    Then An automated negative test suite should run for "<rates>" rate and "<base>" base which will assert the response
    Examples:
      | rates                            | base    |
      | MXN,CZK,GBP,CAD,AUD,USD,JPY,test |         |
      |                                  | USD,PLN |
      | XXX                              |         |
      | XXX                              | YYY     |
      |                                  | YYY     |


  Scenario Outline: 4 - Check Rates API response for "<Specific date>" date
    Given Rates API for "<Specific date>" Foreign Exchange rates
    When The API is available
    Then An automated test suite should run which will assert the success status of the response
    Examples:
      | Specific date |
      | 2010-01-12    |
      | 2020-03-15    |


  Scenario Outline: 5 - Check Rates API response correctness for "<rates>" rate and "<base>" base on "<Specific date>" date
    Given Rates API for "<Specific date>" Foreign Exchange rates
    When The API is available
    Then An automated test suite should run for "<Specific date>" "<rates>" rate and "<base>" base which will assert the response
    Examples:
      | rates   | base | Specific date |
      | USD     | EUR  | 2010-01-12    |
      | GBP     | PLN  | 2020-03-11    |
      | EUR,GBP | PLN  | 2019-04-22    |
      |         |      | 2018-04-01    |
      | USD     |      | 2020-01-01    |
      |         | PLN  | 1999-12-12    |
      | USD,GBP |      | 2010-01-12    |


  Scenario Outline: 6 - Rates API for "<future date>" Foreign Exchange rates
    Given Rates API for "<future date>" Foreign Exchange rates
    When The API is available
    Then An automated test suite should run for "<future date>" which will validate that the response matches for the current date
    Examples:
      | future date |
      | 2030-01-12  |
      | 2099-03-11  |
      | 2251-04-22  |


  Scenario: 7 - Check if Rates API is available for incorrect "<url>"
    Given Rates API for Latest Foreign Exchange rates with "<url>"
    When The API is available
    Then Test case should assert the correct response supplied by the call for "<url>"
    Examples:
      | url                                |
      | https://api.ratesapi.io/api/       |
      | https://api.ratesapi.io/           |
      | https://api.ratesapi.io/randomdata |


