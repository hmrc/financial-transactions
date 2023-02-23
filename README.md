# financial-transactions

## Summary
This protected microservice processes authenticated requests to retrieve Financial Transactions (Liabilities, Payments and Penalties) for a specific Tax Regime for a specified Taxpayer.

The service currently supports the following Tax Regimes:

* Making Tax Digital - VAT (VATC)

## Endpoint Definitions (APIs)

### Get Taxpayer Financial Transactions for Tax Regime

**Method**: GET

**Path**: /financial-transactions/`idType`/`idValue`

|Path Parameter|Description|    
|-|-|    
|`idType`|Used to specify the Tax Regime to retrieve Financial Transactions for. Valid values are: `vat`|    
|`idValue`|Regime Identifier for the Taxpayer. For `vat` this is the VRN|

**Query Parameters**:

|Query Parameter|Mandatory|Description|Format/Valid Values|    
|-|-|-|-|    
|`dateFrom`|**false**|Used to filter the response to only include items from this date|YYYY-MM-DD|    
|`dateTo`|**false**|Used to filter the response to only include items before this date|YYYY-MM-DD|    
|`onlyOpenItems`|**false**|Used to filter the response to only include items that are outstanding/open. If this is not provided then it will default to **false**|`true` \| `false`|

#### Success Response

##### FinancialTransactions object
|Data Item|Type|Mandatory|    
|-|-|-|  
|financialTransactions|`Array[FinancialDetails]`|**true**|
|hasOverdueChargeAndNoTTP|`Boolean`|**true**|

The `hasOverdueChargeAndNoTTP` boolean will be `true` if a user has at least one overdue charge AND no payment lock reason of "Collected via TTP". In all other cases it will be `false`.

##### FinancialDetails object
|Data Item|Type|Mandatory|    
|-|-|-|    
|chargeType|`String`|**false**|    
|periodKey|`String`|**false**|    
|taxPeriodFrom|`Date`|**false**|    
|taxPeriodTo|`Date`|**false**|    
|chargeReference|`String`|**false**|    
|mainTransaction|`String`|**false**|    
|subTransaction|`String`|**false**|    
|originalAmount|`Decimal`|**false**|    
|outstandingAmount|`Decimal`|**false**|    
|clearedAmount|`Decimal`|**false**|    
|items|`Array[SubItem]`|**false**|  
|accruingInterestAmount|`Decimal`|**false**|
|accruingPenaltyAmount|`Decimal`|**false**|  
|penaltyType|`String`|**false**|

##### SubItem object
|Data Item|Type|Mandatory|    
|-|-|-|     
|dueDate|`Date`|**false**|    
|amount|`Decimal`|**false**|    
|clearingDate|`Date`|**false**|    
|clearingReason|`String`|**false**|    
|clearingSAPDocument|`String`|**false**|    
|DDcollectionInProgress|`Boolean`|**false**|

#### Example

Status: OK (200)

Response Body:
```
{    
    "financialTransactions": [      
        {      
            "chargeType" : "VAT Return Debit Charge",    
            "periodKey" : "18AA",    
            "taxPeriodFrom" : "2018-02-01",    
            "taxPeriodTo" : "2018-04-30",    
            "chargeReference" : "XD002750002155",    
            "mainTransaction" : "4700",    
            "subTransaction" : "1174",    
            "originalAmount" : 100.00,    
            "outstandingAmount" : 0.00,  
            "clearedAmount" : 100.00,  
            "items" : [  
                {  
                    "dueDate" : "2018-06-07",  
                    "amount" : 100.00,  
                    "clearingDate" : "2018-05-30",  
                    "clearingReason" : "01",  
                    "clearingSAPDocument" : "001410000442",  
                    "DDcollectionInProgress" : false  
                }    
            ],  
            "accruingInterestAmount" : 55.55,  
            "accruingPenaltyAmount" : 66.66,  
            "penaltyType" : "LPP1"  
        }  
    ],
    "hasOverdueChargeAndNoTTP": true
}    
```   
#### Error Response

##### Failures object
|Data Item|Type|Mandatory|    
|-|-|-|  
|failures|`Array[Error]`|**true**|

##### Error object
|Data Item|Type|Mandatory|    
|-|-|-|  
|code|`String`|**true**|    
|reason|`String`|**true**|

#### Example

Status: NOT_FOUND (404)

Response body:
```  
{  
    "failures" : [  
        {  
            "code" : "NO_DATA_FOUND",  
            "reason" : "The remote endpoint has indicated that no data can be found."  
        }  
    ]  
}  
```  
  ---
### Check Direct Debit exists for VRN

**Method**: GET

**Path**: /has-direct-debit/`vrn`

|Path Parameter|Description|    
|-|-|    
|`vrn`|VAT Registration Number, a 9-digit number|

#### Success Response

##### Response object

|Data Item|Type|Mandatory|    
|-|-|-|    
|directDebitMandateFound|`String`|**true** |
|directDebitDetails|`Array[DirectDebitDetails]`|**false**|

##### DirectDebitDetails object

|Data Item|Type|Mandatory|    
|-|-|-|    
|directDebitInstructionNumber|`String`|**true**|    
|directDebitPlanType|`String`|**true**|    
|dateCreated|`String`|**true**|    
|accountHolderName|`String`|**true**|    
|sortCode|`String`|**true**|    
|accountNumber|`String`|**true**|


#### Example

Status: OK (200)

Response Body:
```  
{  
    "directDebitMandateFound" : true,
    "directDebitDetails" : [  
        {
            "directDebitInstructionNumber": "000000001234567898",
            "directDebitPlanType": "VPP",
            "dateCreated": "2018-04-08",
            "accountHolderName": "PEPSI MAC",
            "sortCode": "406082",
            "accountNumber": "87654321"
        } 
    ]  
}  
```  
---   
### Get Taxpayer penalty details for Tax Regime

**Method**: GET

**Path**: /financial-transactions/penalty/`regime`/`identifier`

|Path Parameter|Description|    
|-|-|    
|`regime`|Used to specify the Tax Regime to retrieve Financial Transactions for. Valid values are: `vat`|    
|`identifier`|Regime Identifier for the Taxpayer. For `vat` the VRN|

**Query Parameters**:

|Query Parameter|Mandatory|Description|Format/Valid Values|    
|-|-|-|-|    
|`dateLimit`|**false**|Number of months data need to be limited to. example 9. This will be expected to be 24 months unless specified.|

#### Success Response

##### PenaltyDetails object

|Data Item|Type|Mandatory|    
|-|-|-|    
|LPPDetails|`Array[LatePaymentPenalty]`|**true**|
|breathingSpace|`Boolean`|**true**|

##### LatePaymentPenalty object

|Data Item|Type|Mandatory|    
|-|-|-|    
|principalChargeReference|`String`|**true**|    
|penaltyCategory|`String`|**true**|    
|LPP1LRCalculationAmount|`BigDecimal`|**false**|    
|LPP1LRDays|`String`|**false**|    
|LPP1LRPercentage|`Double`|**false**|    
|LPP1HRCalculationAmount|`BigDecimal`|**false**|    
|LPP1HRDays|`String`|**false**|    
|LPP1HRPercentage|`Double`|**false**|    
|LPP2Days|`String`|**false**|    
|LPP2Percentage|`Double`|**false**|    
|penaltyChargeReference|`String`|**false**|
|timeToPay|`Boolean`|**true**|

#### Example

Status: OK (200)

Response Body:
```
{    
    "LPPDetails": [    
        {    
            "principalChargeReference": "ABCDEFGHIJKLMNOP",    
            "penaltyCategory": "LPP1",    
            "LPP1LRCalculationAmount": 100.11,    
            "LPP1LRDays": "15",    
            "LPP1LRPercentage": 2.4,    
            "LPP1HRCalculationAmount": 200.22,    
            "LPP1HRDays": "30",    
            "LPP1HRPercentage": 4.2,    
            "LPP2Days": "31",    
            "LPP2Percentage": 5.5,    
            "penaltyChargeReference": "BCDEFGHIJKLMNOPQ" ,
            "timeToPay": false   
        }    
    ],
    "breathingSpace": false
}
```   
#### Error Response

##### Response Object

Errors will be generated based on data from the downstream API call, where `code` contains the HTTP status code and the `reason` contains the JSON body.

## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a JRE to run.

## Running the application

### Running via Service Manager
```
sm2 --start FINANCIAL_TRANSACTIONS    
```   
### Running via SBT

In order to run this microservice, you must have SBT installed. You should then be able to start the application using:

```./run.sh```
  
## Testing the application

To test the application fully (Unit Tests, Component Tests (integration) and Scala Coverage report) execute:
```
sbt clean coverage test it:test coverageReport    
```
*(To run only a subset of the tests omit the desired sbt options accordingly)*

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")