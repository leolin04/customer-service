# Service Scheduler Restful API Implementation

This service is implemented with Java (version 17), Spring Boot 2.7, and MySQL. 
The build framework is Maven 3, and Docker is used to run and test the service. 

## Data Models
* com.cat.customerservice.api.Customer 
  * This is the Customer api model used by the restful client to check in the customers and get next customer. 
  * The `ticketId` is populated after a customer checked in. 
  
* com.cat.customerservice.entity.CustomerEntity
  * This is the Customer data persistence model to save the customers' records to database.
  * The auto increment customer `id` is used as the `ticketId` in the api view. 
  * When a customer checks in, the customer will have `ServingStatus.CHECK_IN` status.
  * When a customer is returned from `ServiceScheduler.getNextCustomer(),` the status is updated to `ServingStatus.SERVED`. 
  
* com.cat.customerservice.entity.CustomerServiceCounterEntity
  * This is a utility data model to keep tracking of the number of VIP and Regular customers served. 
  * The `ServiceScheduler` uses the counts to get next customer with 2:1 ratio of VIPs : Regulars.

## Business Logic
* com.cat.customerservice.service.ServiceScheduler
  * This class contans the checkin and get next customer business logic. 
  * `checkIn(Customer)` method will check in a customer, and saves it to db, and assigns the `ticketId`.
  * `getNextCustomer()` method will try to get the VIP customers first, and then Regular customers. 
  * `getNextCustomer21()` method will try go the next customer with 2:1 ratio of VIPs : Regulars.
    * When there is no more customers with desired customer type (VIP or Regular), then it will try to 
      get the next customer from other customer type. 
  
* 2:1 Ratio Algorithm
  * The service keeps track of the number of VIPs and Regulars served in db.
  1. When the Regulars served == 0, and VIPs served < 2, then return next VIP.
  2. When the Regulars served == 0, and VIPs served >= 2, then return next Regular.
  3. When (VIPs / Regular) ratio <= 2, then return next VIP.
  4. When (VIPs / Regular) ratio > 2, then return next Regular.
  5. If there is no customer from the desired customer type, then it will try to get next customer from
     the other customer type. 
     
## Restful API
* com.cat.customerservice.controller.CustomerServiceController
  * The server is using port `9080` to avoid conflicting with the popular `8080` port.  
  * POST `/v1/checkin` endpoint to check in a customer.
  * GET `/v1/nextcustomer` endpoint to get next VIP customers first, and then regular customers.
    * If there is no more customers, then a dummy customer with `{ticketId: -1}` is returned instead of HTTP 204. 
  * GET `/v1/nextcustomer21` endpoint to get the next customer with 2:1 ratio of VIPs : Regulars.
    * If there is no more customers, then a dummy customer with `{ticketId: -1}` is returned instead of HTTP 204.
  * OpenAPI/Swagger is used to generate the APIs 
    * swagger-ui: `http://localhost:9080/openapi/swagger-ui.html`
    * api-docs: `http://localhost:9080/openapi/v3/api-docs`

## Code Packages
* `com.cat.customerservice.service` package contains the business service classes.
* `com.cat.customerservice.entity` package contains the data persistence models.
* `com.cat.customerservice.repository` package contains the db operations.
* `com.cat.customerservice.controller` package contains the restful resource endpoints.
* `com.cat.customerservice.api` package contains the api data models. 
* `com.cat.customerservice.exception` package contains the customized exceptions which are globally handled by
   the  `GlobalControllerExcceptionHandler` inside the `com.cat.customerservice.handle` package.
* Unit and Integration Tests are in the `src/test` folder. 

## How to Build and Run the code
To build and run the project requires Java (version 17), MySQL, Maven 3, and Docker. 

### To Build the java code, run the following maven command inside the project directory.
```
mvn clean package
```
The tests will run before the packaging, and the tests use the `testcontainers`, 
so the Docker is required for the build process. 

### To Run the code, run the following docker-compose command inside the project directory.
```
docker-compose build && docker-compose up -d
```
If the containers successfully come up, then we can try out the service with the swagger ui at:
http://localhost:9080/openapi/swagger-ui.html

### To shutdown the containers
```
docker-compose down
```

## Other Improvements
* We will use a static db schema in production instead of the dynamic generated and updated.
* We will add monitoring logic, like prometheus.
* We will protect the api with oauth and oauth token (like JWT) in production
* We can improve performance by using async and non-blocking logic in the rest controller and in the business service.
* We need to improve the logging logic, and collect the log. 
* We can run multiple instances of the service with a db replica set. 
* We need to further clarify the stages of the customer status from `checkin` `in-serving` and `served`.
  The current simplified logic only transit the customer from `checkin` to `served`. 