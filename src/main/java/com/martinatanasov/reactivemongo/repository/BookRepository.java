package com.martinatanasov.reactivemongo.repository;

import com.martinatanasov.reactivemongo.domain.Book;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {

    Mono<Book> findFirstByBookName(String name);

    Flux<Book> findByBookAuthor(String author);

    Mono<Boolean> existsByBookAuthorAndPagesAndBookName(String bookAuthor, Integer pages, String bookName);

}
