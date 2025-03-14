package com.exam.dal.dto;

import com.exam.dal.model.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private String password;

    private Role role;
}
