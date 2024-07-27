package com.martinatanasov.reactivemongo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Book {

    @Id
    private String id;
    private String bookName;
    private String category;
    private String author;
    private Integer pages;
    private BigDecimal price;
    private LocalDateTime created;
    private LocalDateTime modified;

}
