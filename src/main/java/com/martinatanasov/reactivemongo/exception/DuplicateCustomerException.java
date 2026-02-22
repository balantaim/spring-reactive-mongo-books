package com.martinatanasov.reactivemongo.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String message) {
        super(message);
    }
}
