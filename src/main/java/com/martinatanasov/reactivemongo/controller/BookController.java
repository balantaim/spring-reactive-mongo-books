package com.martinatanasov.reactivemongo.controller;

import com.martinatanasov.reactivemongo.domain.Book;
import com.martinatanasov.reactivemongo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    @GetMapping("/{bookId}")
    public Mono<Book> getBookById(@PathVariable String bookId){
        return bookRepository.findById(bookId);
    }



}
