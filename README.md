# best-and-less-sapcc-coding-test - SAP Commerce Cloud

This repository contains custom files and folders, custom extensions that are required to set up custom SAP Commerce Cloud feature "Subscribe for notification-NOTIFY ME when product is Back in Stock by registering interest in the product by sharing email address".

This branch covers implmentation on **Commerce Cloud 2211.21**.


# Required OOTB SAP Commerce Cloud Module and extensions
> [!NOTE]
>
>
> This implementation is dependant and using OOTB SAP Commerce Cloud "Customer Interests Module" and its following extensions:

1. customerinterestsocc  - hybris\bin\modules\customer-interest\customerinterestsocc
2. customerinterestsfacades - hybris\bin\modules\customer-interest\customerinterestsfacades
3. customerinterestsservices - hybris\bin\modules\customer-interest\customercouponservices
4. stocknotificationservices - hybris\bin\modules\stock-notification\stocknotificationservices
5. notificationfacades - hybris\bin\modules\notification\notificationfacades
6. notificationservices - hybris\bin\modules\notification\notificationservices

# Implementation - Using Minimum custom code
> Minimum customised code and most of OOTB SAP Commerce Cloud module '"Customer Interests Module"' implementation OCC, Facades and Service API has been used to achieve the required functionality.
> Following new extensions and code has been implemented.

1. bestandlesscustomerinterestsocc
   - au.com.bestandless.customerinterestsocc.controllers.customerinterests.BestAndLessProductInterestsController.java
2. bestandlesscustomerinterestsfacades
   - au.com.bestandless.customerinterestsfacades.customer.BestAndlLessCustomerFacade.java
   - au.com.bestandless.customerinterestsfacades.customer.impl.BestAndlLessCustomerFacadeImpl.java
   -  au.com.bestandless.customerinterestsfacades.customer.impl.BestAndlLessCustomerFacadeImplTest.java
   
3. bestandlesscustomerinterestsocctests- OCC REST API test Spock framework extension to write Test Cases

# System testing and demo details
> OOTB SAP Commerce Cloud 'electronics-spa' Site and data is used for testing and demo.
> Part1 - To create Product Interest based on user input email id.
1. Swagger - Newly implemented POST Rest endpoint https://localhost:9002/occ/v2/swagger-ui/index.html#/Product%20Interests/registerProductInterest
 
> Part2 - To create Product Interest based on user input email id.

2. Backoffice - To update stock of a product as InStock or OutOfStock and to run OOTB SAP Commerce Cloud cron job 'stockLevelStatusCronJob' to send email notification to the registered customers , once product is 'BACK IN STOCK'.
   - 'stockLevelStatusCronJob' cron job defined in OOTB SAP Commerce Cloud extension "stocknotificationservices"

3. Email server - Papercut SMPT for local email testing.

4. Junits - For Unit testing.

### Access the Cloud Portal for deployments

Log in to the Cloud  Portal and verify that your code repository is connected.
1. You have a public-facing code repository.
2. You have an active SAP Commerce Cloud subscription.
3. You have a license for a [supported SAP Commerce release](https://help.sap.com/viewer/dc198ac31ba24dce96149c8480be955f/latest/en-US/1c6c687ad0ed4964bb43d409818d23a2.html)
4. From a supported browser, log in to https://portal.commerce.ondemand.com. For more information, see [Accessing the Cloud Portal](https://help.sap.com/viewer/0c2050f6d31f49ddb6eba18509060ae5/latest/en-US/bc745004669445478d0c0505d77e096c.html).
5. Select *Repository* and verify that you are connected to the correct code repository.
6. Find the environments that were provisioned for your subscription.
7. Create a new build.
8. Deploy the build to the environment using the *Initialze Database* option.




