package com.example.usermanagementsystem.usermanagementsystem.Repository;

import com.example.usermanagementsystem.usermanagementsystem.Entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Integer> {

    Optional<UserInfo> findByEmailAndIsActive(String email, boolean isActive);

    Page<UserInfo> findByNameContainingIgnoreCaseAndIsActive(Pageable pageable, String search, boolean isActive);

    Optional<UserInfo> findByIdAndIsActive(Integer id, boolean isActive);
}
