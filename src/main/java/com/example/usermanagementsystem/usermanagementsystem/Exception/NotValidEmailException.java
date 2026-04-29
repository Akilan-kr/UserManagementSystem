package com.example.usermanagementsystem.usermanagementsystem.Exception;

public class NotValidEmailException extends RuntimeException{
    public  NotValidEmailException(String message){
        super(message);
    }
}
