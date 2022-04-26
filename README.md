# Financial-Transactions

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Download](https://api.bintray.com/packages/hmrc/releases/financial-transactions/images/download.svg)](https://bintray.com/hmrc/releases/financial-transactions/_latestVersion)

This protected micro-service processes authenticated requests to retrieve Financial Transactions (Liabilities, Payments and Penalties) for a specific Tax Regime for a specified Taxpayer.

The service currently supports the following Tax Regimes:

  * Making Tax Digital - VAT (VATC)
  
# Endpoint Definitions (APIs)

## Get Taxpayer Financial Transactions for Tax Regime

**Method**: GET

**URL**: /financial-transactions/`regime`/`identifier`

|Path Parameter|Description|
|-|-|
|`regime`|Used to specify the Tax Regime to retrieve Financial Transactions for. Valid values are: `vat`|
|`identifier`|Regime Identifier for the Taxpayer. For `vat` the VRN|

**Query Parameters**:

|Query Parameter|Mandatory|Description|Format/Valid Values|
|-|-|-|-|
|`fromDate`|**false**|Used to filter the response to only include items from this date|YYYY-MM-DD|
|`toDate`|**false**|Used to filter the response to only include items before this date|YYYY-MM-DD|
|`onlyOpenItems`|**false**|Used to filter the response to only include items that are outstanding/open|`true` \| `false`|
|`includeStatistical`|**false**|Used to filter the response to include statistical items|`true` \| `false`|
|`includeLocks`|**false**|Used to filter the response to include items that have locks|`true` \| `false`|
|`calculateAccruedInterest`|**false**|Calculate accrued interest for overdue debits and include it in the response|`true` \| `false`|
|`customerPaymentInformation`|**false**|Include Taxpayer Payment Information in the response|`true` \| `false`|

**\*** If `onlyOpenItems` is supplied as a query parameter and its value is **false** then `fromDate` and `toDate` become mandatory.

### Success Response

**Status**: OK (200)

#### Definition

##### Response Object

|Data Item|Type|Mandatory|
|-|-|-|
|documentDetails|`Array[DocumentDetailsObject]`|**true**
|financialDetails|`Array[FinancialDetailsObject]` *see below*|**true**|

##### Document Details Object

|Data Item|Type|Mandatory|
|-|-|-|
|taxYear|`String`|**true**|
|documentId|`String`|**true**|
|documentDate|`Date`|**true**|
|documentText|`String`|**true**|
|documentDueDate|`Date`|**true**|
|totalAmount|`Decimal`|**true**|
|documentOutstandingAmount|`Decimal`|**true**|
|statisticalFlag|`Boolean`|**true**|
|accruingPenaltyLPP1|`String`|**false**|
|accruingPenaltyLPP2|`String`|**false**|

##### Financial Transaction Object

|Data Item|Type|Mandatory|
|-|-|-|
|documentId|`String`|**true**|
|chargeType|`String`|**false**|
|mainType|`String`|**false**|
|periodKey|`String`|**false**|
|periodKeyDescription|`String`|**false**|
|taxPeriodFrom|`Date`|**false**|
|taxPeriodTo|`Date`|**false**|
|businessPartner|`String`|**false**|
|contractAccountCategory|`String`|**false**|
|contractAccount|`String`|**false**|
|contractObjectType|`String`|**false**|
|contractObject|`String`|**false**|
|sapDocumentNumber|`String`|**false**|
|sapDocumentNumberItem|`String`|**false**|
|chargeReference|`String`|**false**|
|mainTransaction|`String`|**false**|
|subTransaction|`String`|**false**|
|originalAmount|`Decimal`|**false**|
|outstandingAmount|`Decimal`|**false**|
|clearedAmount|`Decimal`|**false**|
|accruedInterest|`Decimal`|**false**|
|items|`Array[SubItemObject]` *see below*|**false**|

##### Sub-Item Object

|Data Item|Type|Mandatory|
|-|-|-|
|subItem|`String`|**false**|
|dueDate|`Date`|**false**|
|amount|`Decimal`|**false**|
|clearingDate|`Date`|**false**|
|clearingReason|`String`|**false**|
|outgoingPaymentMethod|`String`|**false**|
|paymentLock|`String`|**false**|
|clearingLock|`String`|**false**|
|interestLock|`String`|**false**|
|dunningLock|`String`|**false**|
|returnFlag|`Boolean`|**false**|
|paymentReference|`String`|**false**|
|paymentAmount|`Decimal`|**false**|
|paymentMethod|`String`|**false**|
|paymentLot|`String`|**false**|
|paymentLotItem|`String`|**false**|
|clearingSAPDocument|`String`|**false**|
|statisticalDocument|`String`|**false**|
|DDcollectionInProgress|`Boolean`|**false**|
|returnReason|`String`|**false**|
|promiseToPay|`String`|**false**|

#### Example

**Status**: OK (200)
**Json Body**: 
```
{
    "documentDetails": [
      {
        "taxYear": "2018",
        "documentId": "ABCD",
        "documentDate": "2018-01-01",
        "documentText": "Charge",
        "documentDueDate": "2018-02-02",
        "totalAmount": 99.99,
        "documentOutstandingAmount": 99.99,
        "statisticalFlag": false
      }
    ],
    "financialDetails" : [
      {
        "taxYear": "2018",
        "documentId": "ABCD",
        "chargeType" : "VAT Return Credit Charge",
        "mainType" : "VAT Return Charge",
        "periodKey" : "18AA",
        "periodKeyDescription" : "ABCD",
        "taxPeriodFrom" : "2018-02-01",
        "taxPeriodTo" : "2018-04-30",
        "businessPartner" : "0",
        "contractAccountCategory" : "99",
        "contractAccount" : "X",
        "contractObjectType" : "ABCD",
        "contractObject" : "0",
        "sapDocumentNumber" : "0",
        "sapDocumentNumberItem" : "0",
        "chargeReference" : "XD002750002155",
        "mainTransaction" : "1234",
        "subTransaction" : "5678",
        "originalAmount" : -100.00,
        "outstandingAmount" : -100.00,
        "items" : [
          {
            "subItem" : "000",
            "dueDate" : "2018-06-07",
            "amount" : -100.00
          }
        ]
      }
    ]
  }
}
```

## Check Direct Debit exists for VRN

**Method**: GET

**URL**: /has-direct-debit/`vrn`

|Path Parameter|Description|
|-|-|
|`vrn`|VAT Registration Number, a 9-digit number|



### Success Response

**Status**: OK (200)

#### Definition

##### Response Object

|Data Item|Type|Mandatory|
|-|-|-|
|directDebitMandateFound|`String`|**true**
|directDebitDetails|`Array[DirectDebitDetailsObject]` *see below*|**false**|

##### Financial Transaction Object

|Data Item|Type|Mandatory|
|-|-|-|
|directDebitInstructionNumber|`Boolean`|**true**|
|directDebitPlanType|`String`|**true**|
|dateCreated|`String`|**true**|
|accountHolderName|`String`|**true**|
|sortCode|`String`|**true**|
|accountNumber|`String`|**true**|


#### Example

**Status**: OK (200)

The service responds with a simple true or false response depending on whether a direct debit mandate is found for the vrn specified.
**Json Body**: 
```
true
```

## Get Taxpayer penalty details for Tax Regime

**Method**: GET

**URL**: /financial-transactions/penalty/`regime`/`identifier`

|Path Parameter|Description|
|-|-|
|`regime`|Used to specify the Tax Regime to retrieve Financial Transactions for. Valid values are: `vat`|
|`identifier`|Regime Identifier for the Taxpayer. For `vat` the VRN|

**Query Parameters**:

|Query Parameter|Mandatory|Description|Format/Valid Values|
|-|-|-|-|
|`dateLimit`|**false**|Number of months data need to be limited to. example 9. This will be expected to be 24 months unless specified.|

### Success Response

**Status**: OK (200)

#### Definition

##### Penalty details Object

|Data Item|Type|Mandatory|
|-|-|-|
|lateSubmissionPenalty|`object`|**false**
|latePaymentPenalty|`object`|**false**

##### Late Submission Penalty Object

|Data Item|Type|Mandatory|
|-|-|-|
|summary|`object`|**true**|
|details|`object`|**true**|

##### Late Submission Penalty Summary Object

|Data Item|Type|Mandatory|
|-|-|-|
|activePenaltyPoints|`Int`|**true**|
|inactivePenaltyPoints|`Int`|**true**|
|POCAchievementDate|`Date`|**true**|
|regimeThreshold|`Int`|**true**|
|penaltyChargeAmount|`Decimal`|**true**|

##### Late Submission Penalty Details Object

|Data Item|Type|Mandatory|
|-|-|-|
|penaltyNumber|`String`|**true**|
|penaltyOrder|`String`|**true**|
|penaltyCategory|`String`|**true**|
|penaltyStatus|`String`|**true**|
|penaltyCreationDate|`Date`|**true**|
|penaltyExpiryDate|`Date`|**true**|
|communicationsDate|`Date`|**true**|
|lateSubmissions|`Array[LateSubmissionObject]`|**false**|
|appealStatus|`String`|**false**|
|appealLevel|`String`|**false**|
|chargeReference|`String`|**false**|
|chargeAmount|`Decimal`|**false**|
|chargeOutstandingAmount|`Decimal`|**false**|
|chargeDueDate|`Date`|**false**|

##### Late Submission Object

|Data Item|Type|Mandatory|
|-|-|-|
|lateSubmissionID|`String`|**true**|
|taxPeriod|`Date`|**false**|
|taxReturnStatus|`Decimal`|**true**|
|taxPeriodStartDate|`Date`|**false**|
|taxPeriodEndDate|`Date`|**false**|
|taxPeriodDueDate|`Date`|**false**|
|returnReceiptDate|`Date`|**false**|

##### Late Payment Penalty Object
\|Data Item|Type|Mandatory|
|-|-|-|
|details|`object`|**true**|

##### Late Payment Penalty details Object

|Data Item|Type|Mandatory|
|-|-|-|
|penaltyNumber|`String`|**true**|
|penaltyCategory|`String`|**true**|
|penaltyStatus|`String`|**true**|
|penaltyAmountAccruing|`Decimal`|**true**|
|penaltyAmountPosted|`Decimal`|**true**|
|penaltyChargeCreationDate|`Date`|**true**|
|communicationsDate|`Date`|**true**|
|penaltyChargeReference|`String`|**true**|
|penaltyChargeDueDate|`Date`|**true**|
|appealStatus|`String`|**false**|
|appealLevel|`String`|**false**|
|principalChargeReference|`String`|**true**|
|principalChargeDueDate|`Date`|**true**|

#### Example

**Status**: OK (200)
**Json Body**:
```
{
    "lateSubmissionPenalty": {
      "summary": {
        "activePenaltyPoints": 1,
        "inactivePenaltyPoints": 1,
        "POCAchievementDate": "2018-01-01",
        "regimeThreshold": 1,
        "penaltyChargeAmount": -99999999999.99
      },
      "details": [
        {
          "penaltyNumber": "XXXXXXXXXXX1",
          "penaltyOrder": "XX",
          "penaltyCategory": "P",
          "penaltyStatus": "ACTIVE",
          "penaltyCreationDate": "2018-01-01",
          "penaltyExpiryDate": "2018-01-01",
          "communicationsDate": "2018-01-01",
          "lateSubmissions": [
            {
              "lateSubmissionID": "XXX",
              "taxPeriod": "18AA",
              "taxReturnStatus": "XXXXX",
              "taxPeriodStartDate": "2018-01-01",
              "taxPeriodEndDate": "2018-01-01",
              "taxPeriodDueDate": "2018-01-01",
              "returnReceiptDate": "2018-01-01"
            }
          ],
          "appealStatus": "XX",
          "appealLevel": "XX",
          "chargeReference": "XXXXXXXXXXXXXXX1",
          "chargeAmount": -99999999999.99,
          "chargeOutstandingAmount": -99999999999.99,
          "chargeDueDate": "2018-01-01"
        }
      ]
    },
    "latePaymentPenalty": {
      "details": [
        {
          "penaltyNumber": "XXXXXXXXXXX2",
          "penaltyCategory": "LPP1",
          "penaltyStatus": "A",
          "penaltyAmountAccruing": -99999999999.99,
          "penaltyAmountPosted": -99999999999.99,
          "penaltyChargeCreationDate": "2018-01-01",
          "communicationsDate": "2018-01-01",
          "penaltyChargeReference": "XXXXXXXXXXXXXXX2",
          "penaltyChargeDueDate": "2018-01-01",
          "appealStatus": "XX",
          "appealLevel": "XX",
          "principalChargeReference": "XXXXXXXXXXXXXXX3",
          "principalChargeDueDate": "2018-01-01"
        }
      ]
    }
  }
}
```

### Error Response

#### Definition

##### Response Object

|Data Item|Type|Mandatory|
|-|-|-|
|code|`Int`|**true**
|reason|`String`|**true**

#### Example
```
{
  "code": "SERVICE_UNAVAILABLE",
  "reason": "Dependent systems are currently not responding"
}
```

Requirements
------------

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.


## Running the application

### Running from Nexus/Bintray
To update from Nexus and start all services from the RELEASE version instead of snapshot
```
sm --start FINANCIAL_TRANSACTIONS -f
```


### To manually run the application locally:

Kill the service ```sm --stop FINANCIAL_TRANSACTIONS``` *(if it's already running)*. Then run:
```
sbt 'run 9085'
```
To run with **testOnlyRoutes** enabled *(if required)*:
```
sbt "run 9085 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes"
```

## Testing the application

To test the application fully (Unit Tests, Component Tests *(integration)*, Scala Style Checker and Scala Coverage report) execute:
```
sbt clean scalastyle coverage test it:test coverageOff coverageReport
```
*(To run only a subset of the tests omit the desired sbt options accordingly)*


---
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
