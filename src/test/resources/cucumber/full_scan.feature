Feature: Perform a full scan

  Scenario: Perform a full scan
    Given an event manager without listeners
    And scans will stop after one result is found
    When starting a full scan for ip "192.168.1.0"
    Then wait for a MAC address to be found
    And print scan results
