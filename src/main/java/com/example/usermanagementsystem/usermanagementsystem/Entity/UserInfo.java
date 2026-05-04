package com.example.usermanagementsystem.usermanagementsystem.Entity;

import com.example.usermanagementsystem.usermanagementsystem.Enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity(name = "user_table")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "User Name Cannot be null or empty")
    @Pattern(
            regexp = "^[a-zA-Z ]+$",
            message = "Name should only contains Upper Case, Lower Case, Space"
    )
    private String name;
    @NotBlank(message = "Email Cannot be null or empty")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email format is invalid (e.g., example@domain.com)"
    )
    private String email;
    @NotNull(message = "Age cannot be null")
    @Min(value = 18)
    @Max(value = 80)
    private Integer age;
    @NotBlank(message = "Password Cannot be null or Empty")
    @Pattern(
            regexp = "^.*(?=.{8,20})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$$",
            message = "Password should contains minimum 8 maximum 20 character, then it should contain at least 1 Capital letter,1 Small letter, 1 number and 1 special symbol"
    )
    private String password;
    private Roles role;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updateAt;
    private Boolean isActive;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}
