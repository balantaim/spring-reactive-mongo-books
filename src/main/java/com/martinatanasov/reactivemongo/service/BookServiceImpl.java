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
        return bookRepository.findByAuthor(author)
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
            item.setCreated(LocalDateTime.now());
            item.setModified(LocalDateTime.now());
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
        bookDTO.setModified(LocalDateTime.now());
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
        bookDTO.setModified(LocalDateTime.now());
        return bookRepository.findById(id)
                .map(foundBook -> {
                    //update properties
                    foundBook.setBookName(bookDTO.getBookName());
                    foundBook.setCategory(bookDTO.getCategory());
                    foundBook.setAuthor(bookDTO.getAuthor());
                    foundBook.setPages(bookDTO.getPages());
                    foundBook.setPrice(bookDTO.getPrice());

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
                    if(StringUtils.hasText(bookDTO.getCategory())){
                        foundBook.setCategory(bookDTO.getCategory());
                    }
                    //Author
                    if(StringUtils.hasText(bookDTO.getAuthor())){
                        foundBook.setAuthor(bookDTO.getAuthor());
                    }
                    //Pages
                    if(bookDTO.getPages() != null){
                        foundBook.setPages(bookDTO.getPages());
                    }
                    //Price
                    if(bookDTO.getPrice() != null){
                        foundBook.setPrice(bookDTO.getPrice());
                    }
                    //Update modified date
                    foundBook.setModified(LocalDateTime.now());

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
