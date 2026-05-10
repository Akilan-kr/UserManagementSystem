package com.example.usermanagementsystem.usermanagementsystem.Exception;

public class ExceedingStockCountException extends RuntimeException{
    public ExceedingStockCountException(String message){
        super(message);
    }
}
