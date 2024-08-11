# This is book vault application with CRUD functionalities

### It uses asynchronous web client and document based database.

### New Java frontend template is added to the project which uses React under the hood (Vaadin Flow)

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

## Vaadin UI

<table>
    <tr>
        <td><img src="images/sc1.PNG" alt="Get all books view"></td>
    </tr>
    <tr>
        <td><img src="images/sc3.PNG" alt="Add a new book"></td>
    </tr>
    <tr>
        <td><img src="images/sc2.PNG" alt="Delete book"></td>
    </tr>
</table>