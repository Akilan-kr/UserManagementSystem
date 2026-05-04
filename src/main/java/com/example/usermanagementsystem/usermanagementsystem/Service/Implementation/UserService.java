package com.example.usermanagementsystem.usermanagementsystem.Service.Implementation;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.UserResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import com.example.usermanagementsystem.usermanagementsystem.Exception.PartialUserAlreadyAvailable;
import com.example.usermanagementsystem.usermanagementsystem.Exception.UserAlreadyAvailable;
import com.example.usermanagementsystem.usermanagementsystem.Mapper.UserMapper;
import com.example.usermanagementsystem.usermanagementsystem.Repository.UserRepository;
import com.example.usermanagementsystem.usermanagementsystem.Service.Interface.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if(findByEmail(userRequestDto.email()) == null) {
            log.info("Creating User");
            UserInfo userInfo = UserMapper.toEntity(userRequestDto);
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
            userInfo.setIsActive(true);
            return UserMapper.toResponse(userRepository.save(userInfo));
        } else {
            throw new UserAlreadyAvailable("User with this email is already available");
        }
    }


    @Override
    public List<UserResponseDto> createBulkUser(List<UserRequestDto> listOfUserRequestDto) {
        validateDuplicateEmailInTheList(listOfUserRequestDto);
        List<String> duplicateEmails = new ArrayList<>();
        List<UserInfo> validUsers = new ArrayList<>();
        List<UserInfo> userInfos = listOfUserRequestDto.stream().map(UserMapper::toEntity).toList();
        userInfos.forEach( userInfo -> {
            if(findByEmail(userInfo.getEmail()) == null) {
                String encrypt = encoder.encode(userInfo.getPassword());
                userInfo.setPassword(encrypt);
                userInfo.setIsActive(true);
                validUsers.add(userInfo);
            } else
                duplicateEmails.add(userInfo.getEmail());
        });
        if(duplicateEmails.isEmpty())
            return userRepository.saveAll(validUsers).stream().map(UserMapper::toResponse).toList();
        else if(duplicateEmails.size() == userInfos.size())
            throw new UserAlreadyAvailable("All the Emails in the List Request is already Available");
        else
            userRepository.saveAll(validUsers);
            throw new PartialUserAlreadyAvailable("Partial update: Some Email in the List is already available", duplicateEmails);
    }

    private void validateDuplicateEmailInTheList(List<UserRequestDto> listOfUserRequestDto) {
        Set<String> emails = new HashSet<>();
        for(UserRequestDto userRequestDto: listOfUserRequestDto){
            if(!(emails.add(userRequestDto.email()))){
                throw new UserAlreadyAvailable("The List contains duplicate Emails Id, Email should be unique");
            }
        }
    }

    @Override
    public UserResponseDto patchUser(UserPatchDto userPatchDto, Integer id) {
        Optional<UserInfo> userInfo = userRepository.findByIdAndIsActive(id, true);
        if(userInfo.isPresent()){
            UserInfo user = userInfo.get();
            if(userPatchDto.name() != null)
                user.setName(userPatchDto.name());
            if(userPatchDto.age() != null)
                user.setAge(userPatchDto.age());
            if(userPatchDto.email() != null)
                if(findByEmail(userPatchDto.email()) == null) user.setEmail(userPatchDto.email());
                else throw new UserAlreadyAvailable("User with this email is already available");
            if(userPatchDto.role() != null)
                user.setRole(userPatchDto.role());
            return UserMapper.toResponse(userRepository.save(user));
        } else
            throw new UsernameNotFoundException("User not found with id: "+id);
    }

    @Cacheable(value = "allUsers" , key = "#page + '_' + #size +  '_' + #ascending + '_' + #sortBy + '_' + #search" )
    @Override
    public PageResponse<UserResponseDto> getAllUsers(Integer page, Integer size, String sortBy, Boolean ascending, String search) {
        log.info("Getting All Users");
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of((page <= 0) ? 0 : page - 1, size <= 0 ? 5 : size , sort);
        Page<UserInfo> userInfos = userRepository.findByNameContainingIgnoreCaseAndIsActive(pageable, search, true);
        return UserMapper.toPageResponse(userInfos.map(UserMapper::toResponse));
    }


    @Cacheable(value = "users", key = "#id")
    @Override
    public UserResponseDto getUserById(Integer id) {
            log.info("Getting user based on id({})", id);
            Optional<UserInfo> userInfo = userRepository.findByIdAndIsActive(id, true);
            if (userInfo.isPresent()) {
                log.info("User with id({}) presented", id);
                return UserMapper.toResponse(userInfo.get());
            } else {
                log.warn("There is no user with a id:{}", id);
                throw new UsernameNotFoundException("User not found with id: " + id);
            }
    }


    @Override
    public UserResponseDto updateUser(UserRequestDto userRequestDto) {
        log.info("Updating user");
        UserInfo userInfo = UserMapper.toEntity(userRequestDto);
        UserInfo existingUser = findByEmail(userInfo.getEmail());
        if(existingUser != null) {
            log.info("User is presented for updating");
            existingUser.setName(userInfo.getName());
            existingUser.setAge(userInfo.getAge());
            existingUser.setRole(userInfo.getRole());
            existingUser.setPassword(encoder.encode(userInfo.getPassword()));
            return UserMapper.toResponse(userRepository.save(existingUser));
        }
        else {
            log.info("User is not presented in the db, so the user is added as new user");
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
            userInfo.setIsActive(true);
            return UserMapper.toResponse(userRepository.save(userInfo));
        }

    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    public void deleteUserById(Integer id) {
        Optional<UserInfo> userInfo = userRepository.findByIdAndIsActive(id, true);
        if(userInfo.isPresent()) {
            log.warn("User with id({}) is deleted", id);
            userInfo.get().setIsActive(false);
            userRepository.save(userInfo.get());
        } else {
            log.warn("There is no user with a id({}) to delete", id);
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }

    private UserInfo findByEmail(String email){
        Optional<UserInfo> existingUser = userRepository.findByEmailAndIsActive(email, true);
        return existingUser.orElse(null);
    }

}
