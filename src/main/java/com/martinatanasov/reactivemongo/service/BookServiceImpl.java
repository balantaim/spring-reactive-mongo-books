package com.martinatanasov.reactivemongo.service;

import com.martinatanasov.reactivemongo.mapper.BookMapper;
import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Override
    public Flux<BookDTO> findByBookAuthor(String author) {
        return bookRepository.findByBookAuthor(author)
                .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Flux<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Mono<BookDTO> saveBook(Mono<BookDTO> bookDTO) {
        return bookDTO.map(item -> {
            //Set date created and modified
            item.setBookCreated(LocalDateTime.now());
            item.setBookModified(LocalDateTime.now());
            //Return the result
            return  item;
        })
                .map(bookMapper::bookDTOToBook)
                .flatMap(bookRepository::save)
                .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Mono<BookDTO> saveBook(BookDTO bookDTO) {
        //Update the modified date
        bookDTO.setBookModified(LocalDateTime.now());
        return bookRepository.save(bookMapper.bookDTOToBook(bookDTO))
                .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Mono<BookDTO> getById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Mono<BookDTO> updateBook(String id, BookDTO bookDTO) {
        //Update the modified date
        bookDTO.setBookModified(LocalDateTime.now());
        return bookRepository.findById(id)
                .map(foundBook -> {
                    //update properties
                    foundBook.setBookName(bookDTO.getBookName());
                    foundBook.setBookCategory(bookDTO.getBookCategory());
                    foundBook.setBookAuthor(bookDTO.getBookAuthor());
                    foundBook.setPages(bookDTO.getPages());
                    foundBook.setBookPrice(bookDTO.getBookPrice());

                    return foundBook;
                }).flatMap(bookRepository::save)
                .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Mono<BookDTO> patchBook(String id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(foundBook -> {
                    //Name
                    if(StringUtils.hasText(bookDTO.getBookName())){
                        foundBook.setBookName(bookDTO.getBookName());
                    }
                    //Category
                    if(StringUtils.hasText(bookDTO.getBookCategory())){
                        foundBook.setBookCategory(bookDTO.getBookCategory());
                    }
                    //Author
                    if(StringUtils.hasText(bookDTO.getBookAuthor())){
                        foundBook.setBookAuthor(bookDTO.getBookAuthor());
                    }
                    //Pages
                    if(bookDTO.getPages() != null){
                        foundBook.setPages(bookDTO.getPages());
                    }
                    //Price
                    if(bookDTO.getBookPrice() != null){
                        foundBook.setBookPrice(bookDTO.getBookPrice());
                    }
                    //Update modified date
                    foundBook.setBookModified(LocalDateTime.now());

                    return foundBook;
                }).flatMap(bookRepository::save)
                .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Mono<Void> deleteBookById(String id) {
        return bookRepository.deleteById(id);
    }

    @Override
    public Mono<BookDTO> findFirstByBookName(String name) {
        return bookRepository.findFirstByBookName(name)
                .map(bookMapper::bookToBookDTO);
    }
}
