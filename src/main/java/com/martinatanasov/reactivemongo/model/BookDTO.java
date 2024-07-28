package com.martinatanasov.reactivemongo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class BookDTO {

    private String id;

    @NotBlank
    @Size(min = 3, max = 255)
    private String bookName;
    private String bookCategory;
    private String bookAuthor;
    private Integer pages;
    private BigDecimal bookPrice;
    private LocalDateTime bookCreated;
    private LocalDateTime bookModified;

}
