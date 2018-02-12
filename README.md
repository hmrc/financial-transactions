# Financial-Transactions

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Download](https://api.bintray.com/packages/hmrc/releases/financial-transactions/images/download.svg)](https://bintray.com/hmrc/releases/financial-transactions/_latestVersion)

This protected micro-service processes authenticated requests to retrieve Financial Transactions (Liabilities and Payments) for a specific Tax Regime for a specified Taxpayer.

The service currently supports the following Tax Regimes:

  * Making Tax Digital - Income Tax Self-Assessment (ITSA)
  * Making Tax Digital - VAT (VATC)
  

# Endpoint Definitions (APIs)

## Get Taxpayer Financial Transactions for Tax Regime

**Method**: GET

**URL**: /financial-transactions/`regime`/`identifier`

|Path Parameter|Description|
|-|-|
|`regime`|Used to specify the Tax Regime to retrieve Financial Transactions for. Valid values are: `vat` and `it`|
|`identifier`|Regime Identifier for the Taxpayer. For `it` the MTDITID. For `vat` the VRN|

**Query Parameters**:

|Query Parameter|Mandatory|Description|Format/Valid Values|Default Value|
|-|-|-|-|-|
|`fromDate`|**false\***|Used to filter the response to only include items from this date|YYYY-MM-DD|none|
|`toDate`|**false\***|Used to filter the response to only include items before this date|YYYY-MM-DD|none|
|`onlyOpenItems`|**false**|Used to filter the response to only include items that are outstanding/open|`true` \| `false`|**false**|
|`includeLocks`|**false**|Used to filter the response to include items that have locks|`true` \| `false`|**true**|
|`calculateAccruedInterest`|**false**|Calculate accrued interest for overdue debits and include it in the response|`true` \| `false`|**true**|
|`customerPaymentInformation`|**false**|Include Taxpayer Payment Information in the response|`true` \| `false`|**true**|

**\*** If `onlyOpenItems` is **false** then `fromDate` and `toDate` become mandatory.

### Success Response

**Status**: OK (200)

#### Definition

##### Response Object

|Data Item|Type|Mandatory|
|-|-|-|
|idType|`String`|**false**
|idNumber|`String`|**false**
|regimeType|`String`|**false**
|processingDate|`ZonedDateTime`|**true**
|financialTransactions|`Array[FinancialTransactionObject]` *see below*|**false**|

##### Financial Transaction Object

|Data Item|Type|Mandatory|
|-|-|-|
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


#### Example

**Status**: OK (200)
**Json Body**: 
```
{
  "idType": "MTDBSA",
  "idNumber": "XQIT00000000001",
  "regimeType": "ITSA",
  "processingDate": "2017-03-07T09:30:00.000Z",
  "financialTransactions": [
    {
       "chargeType": "PAYE",
       "mainType": "2100",
       "periodKey": "13RL",
       "periodKeyDescription": "abcde",
       "taxPeriodFrom": "1967-08-13",
       "taxPeriodTo": "1967-08-14",
       "businessPartner": "6622334455",
       "contractAccountCategory": "02",
       "contractAccount": "X",
       "contractObjectType": "ABCD",
       "contractObject": "00000003000000002757",
       "sapDocumentNumber": "1040000872",
       "sapDocumentNumberItem": "XM00",
       "chargeReference": "XM002610011594",
       "mainTransaction": "1234",
       "subTransaction": "5678",
       "originalAmount": 10000,
       "outstandingAmount": 10000,
       "clearedAmount": 10000,
       "accruedInterest": 10000,
       "items": [
         {
           "subItem": "001",
           "dueDate": "1967-08-13",
           "amount": 10000,
           "clearingDate": "1967-08-13",
           "clearingReason": "01",
           "outgoingPaymentMethod": "A",
           "paymentLock": "a",
           "clearingLock": "A",
           "interestLock": "C",
           "dunningLock": "1",
           "returnFlag": true,
           "paymentReference": "a",
           "paymentAmount": 10000,
           "paymentMethod": "A",
           "paymentLot": "081203010024",
           "paymentLotItem": "000001",
           "clearingSAPDocument": "3350000253",
           "statisticalDocument": "A"
         }
       ]
     }
   ]
 }
```

### Single-Error Response

#### Definition

##### Response Object

|Data Item|Type|Mandatory|
|-|-|-|
|code|`String`|**true**
|reason|`String`|**true**

#### Example
```
{
  "code": "SERVICE_UNAVAILABLE",
  "reason": "Dependent systems are currently not responding"
}
```

### Multi-Error Response

#### Definition

##### Response Object

|Data Item|Type|Mandatory|
|-|-|-|
|failures|`Array[SingleErrorObject]` *see above*|**true** *(min items 2)*|

#### Example
```
{
  "failures": [
    {
      "code": "INVALID_IDTYPE",
      "reason": "Submission has not passed validation. Invalid parameter idType."
    },
    {
      "code": "INVALID_IDNUMBER",
      "reason": "Submission has not passed validation. Invalid parameter idNumber."
    }
  ]
}
```


### Error Responses

#### Client Triggered Exceptions

|HTTP Code|Code|Reason|
|-|-|-|
|400|BAD_REQUEST|Bad Request. Message: '{error messages}'|
|400|INVALID_TAX_REGIME|The supplied Tax Regime is invalid.|
|401|UNAUTHENTICATED|Not authenticated|
|403|UNAUTHORISED|Not authorised|
|404|NOT_FOUND|URI '{requested path}' not found|

#### Downstream Triggered Exceptions

|HTTP Code|Code|Reason|
|-|-|-|
|400|INVALID_IDTYPE|Submission has not passed validation. Invalid parameter idType.|
|400|INVALID_IDNUMBER|Submission has not passed validation. Invalid parameter idNumber.|
|400|INVALID_REGIMETYPE|Submission has not passed validation. Invalid parameter regimeType.|
|400|INVALID_ONLYOPENITEMS|Submission has not passed validation. Invalid parameter onlyOpenItems.|
|400|INVALID_INCLUDELOCKS|Submission has not passed validation. Invalid parameter includeLocks.|
|400|INVALID_CALCULATEACCRUEDINTEREST|Submission has not passed validation. Invalid parameter calculateAccruedInterest.|
|400|INVALID_CUSTOMERPAYMENTINFORMATION|Submission has not passed validation. Invalid parameter customerPaymentInformation.|
|400|INVALID_DATEFROM|Submission has not passed validation. Invalid parameter dateFrom|
|400|INVALID_DATETO|Submission has not passed validation. Invalid parameter dateTo|
|404|NOT_FOUND|The remote endpoint has indicated that no data can be found|
|422|INVALID_DATA|The remote endpoint has indicated that the request contains invalid data|
|500|SERVER_ERROR|DES is currently experiencing problems that require live service intervention|
|500|INVALID_JSON|The downstream service responded with invalid json.|
|500|UNEXPECTED_JSON_FORMAT|The downstream service responded with json which did not match the expected format.|
|500|UNEXPECTED_DOWNSTREAM_ERROR|The downstream service responded with an unexpected response.|
|503|SERVICE_UNAVAILABLE|Dependent systems are currently not responding|

#### Catch All (e.g. runtime exceptions)

|HTTP Code|Code|Reason|
|-|-|-|
|{status}|{status}|{error message}|

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
*(To run only a subset of the tests ommit the desired sbt options accordingly)*


---
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
