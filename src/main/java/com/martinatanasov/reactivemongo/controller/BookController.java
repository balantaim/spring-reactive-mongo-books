package com.martinatanasov.reactivemongo.controller;

import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/{bookId}")
    public Mono<BookDTO> getBookById(@PathVariable String bookId){
        return bookService.getById(bookId);
    }

    @GetMapping("/all-books")
    public Flux<BookDTO> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/name/{bookName}")
    public Mono<BookDTO> findFirstBookByName(@PathVariable String bookName){
        return bookService.findFirstByBookName(bookName);
    }

    @GetMapping("/author/{author}")
    public Flux<BookDTO> findByBookAuthor(@PathVariable String author){
        return bookService.findByBookAuthor(author);
    }

    @PutMapping("/{id}")
    public Mono<BookDTO> updateBook(@PathVariable String id,
                                    @Valid @RequestBody  BookDTO bookDTO){
        return bookService.updateBook(id, bookDTO);
    }

    @PatchMapping("/{id}")
    public Mono<BookDTO> patchBook(@PathVariable String id,
                                    @Valid @RequestBody  BookDTO bookDTO){
        return bookService.patchBook(id, bookDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBookById(@PathVariable String id){
        return bookService.deleteBookById(id);
    }

}
