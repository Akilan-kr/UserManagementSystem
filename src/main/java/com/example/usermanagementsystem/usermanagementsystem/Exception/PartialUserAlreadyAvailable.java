package com.example.usermanagementsystem.usermanagementsystem.Exception;

import java.util.List;
import java.util.stream.Collectors;

public class PartialUserAlreadyAvailable extends RuntimeException{
    public PartialUserAlreadyAvailable(String message, List<String> duplicateEmails){
        super(message +" "+ String.join(", ", duplicateEmails));
    }
}
