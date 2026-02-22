package com.martinatanasov.reactivemongo.controller;

import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/{bookId}")
    public Mono<BookDTO> getBookById(@PathVariable final String bookId) {
        return bookService.getById(bookId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/all-books")
    public Flux<BookDTO> getAllBooks() {
        return bookService.getAllBooks()
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/name/{bookName}")
    public Mono<BookDTO> findFirstBookByName(@PathVariable String bookName) {
        return bookService.findFirstByBookName(bookName)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/author/{author}")
    public Flux<BookDTO> findByBookAuthor(@PathVariable String author) {
        return bookService.findByBookAuthor(author)
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/")
    public Mono<ResponseEntity<BookDTO>> addBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.saveBook(bookDTO)
                //Return 200 if the resource is updated and the URL for the object
                .map(data -> ResponseEntity.created(
                        UriComponentsBuilder.fromUriString("http://localhost:5000/api/v1/books/" + data.getId()).build().toUri()
                ).body(data));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<BookDTO>> updateBook(@PathVariable final String id,
                                                 @Valid @RequestBody BookDTO bookDTO) {
        return bookService.updateBook(id, bookDTO)
                //Return 404 if book with this ID is not found
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                //Return 200 if the resource is updated
                .map(data -> ResponseEntity.ok().body(data));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<BookDTO>> patchBook(@PathVariable final String id,
                                                @Valid @RequestBody BookDTO bookDTO) {
        return bookService.patchBook(id, bookDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                //Return 200 if the book is updated
                .map(data -> ResponseEntity.ok().body(data));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBookById(@PathVariable final String id) {
        //Check if the book exist
        return bookService.getById(id)
                //If the book doesn't exist return 404
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(book -> bookService.deleteBookById(book.getId()))
                //If the book is deleted return 204
                .thenReturn(ResponseEntity.noContent().build());
    }

}
