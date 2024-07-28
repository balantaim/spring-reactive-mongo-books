# This is book vault application

### It uses asynchronous web client and document based database.

### New frontend framework is added to the project which uses React under the hood

### Tools

Software: Spring, Java 21, WebFlux, Lombok, Awaitility, Mapstruct, Vaadin Flow (UI Framework)

Database: MongoDB (on the locale machine)

### Available tests

BookServiceTest.java

### Endpoint

From BookController: <a href="http://localhost:5000/api/v1/books/">http://localhost:5000/api/v1/books/</a>

From Vaadin Flow (Experimental UI): <a href="http://localhost:5000">http://localhost:5000</a>

## Build production project

mvn clean package -Pproduction

The script will also generate frontend. This process could take some minutes!