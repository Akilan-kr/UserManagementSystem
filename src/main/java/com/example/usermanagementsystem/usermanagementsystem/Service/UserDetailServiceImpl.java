package com.example.usermanagementsystem.usermanagementsystem.Service;

import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String userEmail) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = userRepository.findByEmailAndIsActive(userEmail, true);
        if(userInfo.isEmpty()){
            throw new UsernameNotFoundException("User not found with email: " + userEmail);
        }
        UserInfo presentUser = userInfo.get();
        return new User(presentUser.getEmail(),presentUser.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_"+presentUser.getRole())));
    }
}
