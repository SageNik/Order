package com.example.order.exception;

import lombok.Getter;

@Getter
public class IllegalOrderStatusException extends RuntimeException{

    public IllegalOrderStatusException(String message) {
        super(message);
    }
}
