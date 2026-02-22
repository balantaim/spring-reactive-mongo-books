package com.martinatanasov.reactivemongo.service;

import com.martinatanasov.reactivemongo.domain.Book;
import com.martinatanasov.reactivemongo.mapper.BookMapper;
import com.martinatanasov.reactivemongo.mapper.BookMapperImpl;
import com.martinatanasov.reactivemongo.model.BookDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@Testcontainers
@SpringBootTest
public class BookServiceContainerTest {

    //For Docker
//    @Container
//    @ServiceConnection
//    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookDTO = bookMapper.bookToBookDTO(getTestBook());
    }

    //@Disabled("Disabled test")
    @Test
    @DisplayName("Save a new Book")
    void saveBook() {
        //Create atomic boolean
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        Mono<BookDTO> saveMono = bookService.saveBook(Mono.just(bookDTO));

        saveMono.subscribe(savedDTO -> {
            log.info("\tID: {}", savedDTO.getId());
            atomicBoolean.set(true);
        });

        //Wait until new book is saved
        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Find book by author")
    void testFindByBeerStyle() {
        BookDTO bookDTO = getSavedBookDTO();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        bookService.findByBookAuthor(bookDTO.getBookAuthor())
                .subscribe(dto -> {
                    log.info("\nBook: {}", dto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Test save book by using Subscriber")
    void saveBookUseSubscriber() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<BookDTO> atomicDto = new AtomicReference<>();

        Mono<BookDTO> savedMono = bookService.saveBook(Mono.just(bookDTO));

        savedMono.subscribe(savedDto -> {
            log.info("\nID: {}", savedDto.getId());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        await().untilTrue(atomicBoolean);

        //Get the saved instance of Book
        BookDTO persistedDto = atomicDto.get();
        //The data should be valid
        assertThat(persistedDto).isNotNull();
        assertThat(persistedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Book Using Block")
    void testUpdateBookByBlocking() {
        final String newName = "New Book Name";
        BookDTO savedBookDTO = getSavedBookDTO();
        savedBookDTO.setBookName(newName);

        BookDTO updatedDto = bookService.saveBook(Mono.just(savedBookDTO)).block();

        //Verify exists in DB
        BookDTO fetchedDto = bookService.getById(updatedDto.getId()).block();
        assertThat(fetchedDto.getBookName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test update book by using reactive streams")
    void testUpdateBookByStreaming() {
        final String newName = "New Book Name 2";

        Mono<BookDTO> updatedBook = bookService.saveBook(Mono.just(getTestBookDTO()))
                .map(savedBookDto -> {
                    savedBookDto.setBookName(newName);
                    return savedBookDto;
                })
                .flatMap(savedBookDto -> bookService.updateBook(savedBookDto.getId(), savedBookDto))
                .flatMap(savedUpdatedDto -> bookService.getById(savedUpdatedDto.getId()));

        StepVerifier.create(updatedBook)
                .assertNext(dtoFromDB -> {
                    assertThat(dtoFromDB.getBookName()).isEqualTo(newName);
                }).verifyComplete();
    }

    @Test
    @DisplayName("Delete existing book by ID")
    void testDeleteBook() {
        BookDTO book = getSavedBookDTO();

        bookService.deleteBookById(book.getId()).block();

        Mono<BookDTO> expectedEmptyBookMono = bookService.getById(book.getId());

        BookDTO emptyBook = expectedEmptyBookMono.block();

        assertThat(emptyBook).isNull();
    }

    private BookDTO getSavedBookDTO() {
        return bookService.saveBook(Mono.just(getTestBookDTO())).block();
    }

    private static BookDTO getTestBookDTO() {
        return new BookMapperImpl().bookToBookDTO(getTestBook());
    }

    private static Book getTestBook() {
        return Book.builder()
                .bookName("Spring Security 3")
                .bookCategory("Software")
                .bookAuthor("Dan Vega")
                .pages(332)
                .bookPrice(BigDecimal.TEN)
                .bookCreated(LocalDateTime.now())
                .bookModified(LocalDateTime.now())
                .build();
    }

}
