package com.example.usermanagementsystem.usermanagementsystem.Exception;

public class UserAlreadyAvailable extends RuntimeException{
    public UserAlreadyAvailable(String message){
        super(message);
    }
}
