package com.martinatanasov.reactivemongo.model;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CustomerDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    //@Builder
    public class Customer {

        private String id;

        @NotBlank
        private String customerName;

        private LocalDateTime createdDate;

        private LocalDateTime lastModifiedDate;
    }
}
