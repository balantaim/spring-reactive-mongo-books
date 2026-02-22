package com.martinatanasov.reactivemongo.controller;

import com.martinatanasov.reactivemongo.model.BookDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Get all books")
    @Order(1)
    void getAllBooks(){
        webTestClient.get()
                .uri("/api/v1/books/all-books")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @DisplayName("Book not found")
    @Order(2)
    void findBookByIdNotFound(){
        webTestClient.get()
                .uri("/api/v1/books/5000aaa")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("Create Book - Bad Request")
    void createBookBadRequest(){
        BookDTO testBook = BookDTO.builder()
                .bookName("")
                .build();

        webTestClient.post()
                .uri("/api/v1/books/")
                .body(Mono.just(testBook), BookDTO.class)
                .header("Counter-Type", "application/json")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("Update Book - PATCH - Not found")
    void updateBookPatch(){
        BookDTO testBook = BookDTO.builder()
                .bookName("Test of the dragon2")
                .bookCategory("Test2")
                .bookAuthor("Martin test")
                .pages(264)
                .bookPrice(BigDecimal.TWO)
                .bookCreated(LocalDateTime.now())
                .bookModified(LocalDateTime.now())
                .build();

        webTestClient.patch()
                .uri("/api/v1/books/5000aaa")
                .body(Mono.just(testBook), BookDTO.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("Update Book - Not Found")
    void updateBookNotFound(){
        BookDTO testBook = BookDTO.builder()
                .bookName("Test of the dragon")
                .bookCategory("Test")
                .bookAuthor("Martin test")
                .pages(264)
                .bookPrice(BigDecimal.TWO)
                .bookCreated(LocalDateTime.now())
                .bookModified(LocalDateTime.now())
                .build();

        webTestClient.put()
                .uri("/api/v1/books/67aaa")
                .body(Mono.just(testBook), BookDTO.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("Delete Book - Not Found")
    void deleteBookNotFound(){
        webTestClient.delete()
                .uri("/api/v1/books/67aaa")
                .exchange()
                .expectStatus()
                .isNotFound();
    }


}