Feature: Test

  Scenario: pathMatches('/api/activity') && methodIs('get')
    * def response = { "activity": "Cook something together with someone", "type": "cooking", "participants": 2, "price": 0.3, "link": "", "key": "1799120", "accessibility": 0.8 }

  Scenario:
    * def responseStatus = 404