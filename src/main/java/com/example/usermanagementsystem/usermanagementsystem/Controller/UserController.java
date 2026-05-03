package com.example.usermanagementsystem.usermanagementsystem.Controller;

import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserPatchDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.RequestDTO.UserRequestDto;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ApiResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.PageResponse;
import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.UserResponseDto;
import com.example.usermanagementsystem.usermanagementsystem.Service.CommonUtils;
import com.example.usermanagementsystem.usermanagementsystem.Service.Interface.IUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto userRequestDto){
          log.info("/create API endpoint called");
          return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(userService.createUser(userRequestDto), "Successfully Created", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create/bulkUser")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> createBulkUser(@Valid @RequestBody List<UserRequestDto> listOfUserRequestDto){
            log.info("/create/bulkuser API endpoint called");
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(userService.createBulkUser(listOfUserRequestDto), "Successfully Created Bulk users", true));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "true") boolean ascending) {
        log.info("/getAll API endpoint called");
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of((page <= 0) ? 0 : page - 1, size <= 0 ? 5 : size , sort);
        PageResponse<UserResponseDto> pagedUserResponse = userService.getAllUsers(pageable, name);
        if(!(pagedUserResponse.content().isEmpty()))
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(pagedUserResponse,"Successfully Get all the user", true ));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(pagedUserResponse,"No Users in the list", true ));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Integer id){
        try {
            log.info("/get/id API endpoint called for a id:{}", id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(userService.getUserById(id),"Successfully Get a particular user",true));
        } catch(UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null, "User not Found with id"+id, false));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@Valid @RequestBody UserRequestDto userRequestDto){
            log.info("/update API endpoint called");
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(userService.updateUser(userRequestDto), "Updated Successfully", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Integer id){
        try {
            log.info("/delete/id API endpoint is called for id:{}", id);
            userService.deleteUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(null,"Data Deleted Successfully", true));
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(null,"Data Deleted Successfully", true));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null, "No Data Founded with that ID",false));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> patchUser(@PathVariable Integer id, @RequestBody UserPatchDto userPatchDto){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(userService.patchUser(userPatchDto, id), "Successfully patched", true));
    }
}