# This is book vault application

### It uses asynchronous web client and document based database.

### New frontend framework is added to the project which uses React under the hood

### Tools

Software: Spring, Java 21, WebFlux, Lombok, Awaitility, Mapstruct, Vaadin Flow (UI Framework)

Database: MongoDB (on locale machine)

### Available tests

/src/test/java/com/martinatanasov/reactivemongo/BookServiceTest.java

### Endpoints

From BookController: <a href="http://localhost:5000/api/v1/books/">http://localhost:5000/api/v1/books/</a>
You could use postman collection in folder 'postman'.

From Vaadin Flow (Experimental UI): <a href="http://localhost:5000/vaadin">http://localhost:5000/vaadin</a>

## Build production project

mvn clean package -Pproduction

The script will also generate frontend. This process could take some minutes!