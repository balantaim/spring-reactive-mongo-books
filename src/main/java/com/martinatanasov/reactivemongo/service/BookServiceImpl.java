package com.martinatanasov.reactivemongo.service;

import com.martinatanasov.reactivemongo.exception.DuplicateBookException;
import com.martinatanasov.reactivemongo.mapper.BookMapper;
import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final CacheManager cacheManager;

    @Cacheable(cacheNames = "bookListCache")
    @Override
    public Flux<BookDTO> findByBookAuthor(String author) {
        return bookRepository.findByBookAuthor(author)
                .map(bookMapper::bookToBookDTO);
    }

    @Cacheable(cacheNames = "bookListCache")
    @Override
    public Flux<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .map(bookMapper::bookToBookDTO);
    }

    @Transactional
    @Override
    public Mono<BookDTO> saveBook(Mono<BookDTO> bookDTO) {
        clearBookListCache();
        return bookDTO.map(item -> {
                    //Set date created and modified
                    item.setBookCreated(LocalDateTime.now());
                    item.setBookModified(LocalDateTime.now());
                    //Return the result
                    return item;
                }).map(bookMapper::bookDTOToBook)
                .flatMap(bookRepository::save)
                .map(bookMapper::bookToBookDTO);
    }

    @Transactional
    @Override
    public Mono<BookDTO> saveBook(BookDTO bookDTO) {
        clearBookListCache();
        bookDTO.setBookCreated(LocalDateTime.now());
        bookDTO.setBookModified(LocalDateTime.now());
        return bookRepository.existsByBookAuthorAndPagesAndBookName(bookDTO.getBookAuthor(), bookDTO.getPages(), bookDTO.getBookName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateBookException("Duplicated book: " + bookDTO.getBookName()));
                    }
                    return bookRepository.save(bookMapper.bookDTOToBook(bookDTO));
                }).map(bookMapper::bookToBookDTO);
    }

    @Cacheable(cacheNames = "bookCache")
    @Override
    public Mono<BookDTO> getById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDTO);
    }

    @Transactional
    @Override
    public Mono<BookDTO> updateBook(String id, BookDTO bookDTO) {
        clearBookListCacheAndUpdateBookCache(id);
        return bookRepository.findById(id)
                .map(foundBook -> {
                    //update properties
                    foundBook.setBookName(bookDTO.getBookName());
                    foundBook.setBookCategory(bookDTO.getBookCategory());
                    foundBook.setBookAuthor(bookDTO.getBookAuthor());
                    foundBook.setPages(bookDTO.getPages());
                    foundBook.setBookPrice(bookDTO.getBookPrice());
                    //Update the modified date
                    foundBook.setBookModified(LocalDateTime.now());

                    return foundBook;
                }).flatMap(bookRepository::save)
                .map(bookMapper::bookToBookDTO);
    }

    @Transactional
    @Override
    public Mono<BookDTO> patchBook(String id, BookDTO bookDTO) {
        clearBookListCacheAndUpdateBookCache(id);
        return bookRepository.findById(id)
                .map(foundBook -> {
                    //Name
                    if (StringUtils.hasText(bookDTO.getBookName())) {
                        foundBook.setBookName(bookDTO.getBookName());
                    }
                    //Category
                    if (StringUtils.hasText(bookDTO.getBookCategory())) {
                        foundBook.setBookCategory(bookDTO.getBookCategory());
                    }
                    //Author
                    if (StringUtils.hasText(bookDTO.getBookAuthor())) {
                        foundBook.setBookAuthor(bookDTO.getBookAuthor());
                    }
                    //Pages
                    if (bookDTO.getPages() != null) {
                        foundBook.setPages(bookDTO.getPages());
                    }
                    //Price
                    if (bookDTO.getBookPrice() != null) {
                        foundBook.setBookPrice(bookDTO.getBookPrice());
                    }
                    //Update modified date
                    foundBook.setBookModified(LocalDateTime.now());

                    return foundBook;
                }).flatMap(bookRepository::save)
                .map(bookMapper::bookToBookDTO);
    }

    @Transactional
    @Override
    public Mono<Void> deleteBookById(String id) {
        clearBookListCacheAndUpdateBookCache(id);
        return bookRepository.deleteById(id);
    }

    @Cacheable(cacheNames = "bookCache")
    @Override
    public Mono<BookDTO> findFirstByBookName(String name) {
        return bookRepository.findFirstByBookName(name)
                .map(bookMapper::bookToBookDTO);
    }

    private void clearBookListCacheAndUpdateBookCache(String id) {
        if (cacheManager.getCache("bookListCache") != null) {
            cacheManager.getCache("bookListCache").clear();
        }
        if (cacheManager.getCache("bookCache") != null) {
            cacheManager.getCache("bookCache").evict(id);
        }
    }

    private void clearBookListCache() {
        if (cacheManager.getCache("bookListCache") != null) {
            cacheManager.getCache("bookListCache").clear();
        }
    }

}
