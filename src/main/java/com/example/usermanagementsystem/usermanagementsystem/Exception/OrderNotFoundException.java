package com.example.usermanagementsystem.usermanagementsystem.Exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(String message){
        super(message);
    }
}
