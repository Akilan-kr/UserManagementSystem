package com.example.usermanagementsystem.usermanagementsystem.Service;

import com.example.usermanagementsystem.usermanagementsystem.Exception.NotValidAgeException;
import com.example.usermanagementsystem.usermanagementsystem.Exception.NotValidEmailException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CommonUtils {

    private static final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public Boolean validAge(Integer age){
       if(age > 18 && age < 80)
           return true;
       else
           throw new NotValidAgeException("Age should be age 18 and below 80");
    }

    public Boolean validEmail(String email){
        if(patternMatchForEmail(email)){
            return true;
        } else
            throw new NotValidEmailException("Email Should be in a proper format  eg: example@gmail.com but your request contains this " + email);
    }


    private Boolean patternMatchForEmail(String email){
        return Pattern.compile(emailRegex)
                .matcher(email)
                .matches();
    }

    public @NotBlank(message = "OrderId Cannot be null or blank") String generateOrderId() {
        return "ORD-ID-"+UUID.randomUUID().toString().substring(0,5).toUpperCase();
    }
}
