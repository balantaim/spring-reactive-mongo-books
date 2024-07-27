package com.martinatanasov.reactivemongo;

import com.martinatanasov.reactivemongo.domain.Book;
import com.martinatanasov.reactivemongo.mapper.BookMapper;
import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    BookService bookService;

    @Autowired
    BookMapper bookMapper;

    BookDTO bookDTO;
    @BeforeEach
    void setUp(){
        bookDTO = bookMapper.bookToBookDTO(getTestBeer());
    }

    @Test
    @DisplayName("Save a new Book")
    void saveBook(){
        //Create atomic boolean
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        Mono<BookDTO> saveMono = bookService.saveBook(Mono.just(bookDTO));

        saveMono.subscribe(savedDTO -> {
            System.out.println("\tID: " + savedDTO.getId());
            atomicBoolean.set(true);
        });

        //Wait until new book is saved
        await().untilTrue(atomicBoolean);
    }

    public static Book getTestBeer() {
        return Book.builder()
                .bookName("Spring security 3")
                .category("Software")
                .author("Dan Vega")
                .pages(332)
                .price(BigDecimal.TEN)
                .build();
    }

}
