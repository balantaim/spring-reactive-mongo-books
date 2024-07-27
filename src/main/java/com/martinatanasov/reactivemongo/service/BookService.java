package com.martinatanasov.reactivemongo.service;

import com.martinatanasov.reactivemongo.model.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookService {

    Flux<BookDTO> findByBookAuthor(String author);

    Flux<BookDTO> getAllBooks();

    Mono<BookDTO> saveBook(Mono<BookDTO> bookDTO);

    Mono<BookDTO> saveBook(BookDTO bookDTO);

    Mono<BookDTO> getById(String id);

    Mono<BookDTO> updateBook(String id, BookDTO bookDTO);

    Mono<BookDTO> patchBook(String id, BookDTO bookDTO);

    Mono<Void> deleteBookById(String id);

    Mono<BookDTO> findFirstByBookName(String name);

}
