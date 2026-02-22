package com.martinatanasov.reactivemongo.bootstrap;

import com.martinatanasov.reactivemongo.domain.Book;
import com.martinatanasov.reactivemongo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String[] args) throws Exception {
        bookRepository.deleteAll()
                .doOnSuccess(success -> {
                    loadDefaultBookData();
                })
                .subscribe();
    }

    private void loadDefaultBookData() {

        bookRepository.count().subscribe(count -> {
            if (count == 0) {
                Book book1 = Book.builder()
                        .bookName("House of dragons")
                        .bookCategory("Fantasy")
                        .bookAuthor("Martin")
                        .pages(264)
                        .bookPrice(BigDecimal.TWO)
                        .bookCreated(LocalDateTime.now())
                        .bookModified(LocalDateTime.now())
                        .build();

                Book book2 = Book.builder()
                        .bookName("War Craft - War of the ancient I")
                        .bookCategory("Fantasy")
                        .bookAuthor("Richard A. K.")
                        .pages(444)
                        .bookPrice(BigDecimal.valueOf(30.99))
                        .bookCreated(LocalDateTime.now())
                        .bookModified(LocalDateTime.now())
                        .build();

                Book book3 = Book.builder()
                        .bookName("Photoshop 2019")
                        .bookCategory("Software")
                        .bookAuthor("Adobe corporation")
                        .pages(371)
                        .bookPrice(BigDecimal.valueOf(15.50))
                        .bookCreated(LocalDateTime.now())
                        .bookModified(LocalDateTime.now())
                        .build();

                bookRepository.save(book1).subscribe();
                bookRepository.save(book2).subscribe();
                bookRepository.save(book3).subscribe();
            }
        });
    }
}
