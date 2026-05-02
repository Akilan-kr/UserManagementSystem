package com.example.usermanagementsystem.usermanagementsystem.Controller;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.AuthRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ApiResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.AuthResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> authenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.email(), authRequestDto.password())
            );
            if (authentication.isAuthenticated()) {
                log.info("Token generated for the user: {}", authRequestDto.email());
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(new AuthResponseDto(jwtService.generateToken(authRequestDto.email()), authRequestDto.email()), "Token Created SuccessFully", true));
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }
        } catch (BadCredentialsException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(null, ex.getMessage()+ "Check the password and Email correctly", false));
        } catch (RuntimeException ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(null, ex.getMessage(), false));

        }
    }
}
