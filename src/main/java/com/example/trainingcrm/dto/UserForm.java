package com.example.trainingcrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {

    private Long userId;

    @NotBlank
    @Size(max = 50)
    private String username;

    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String role;

    private Boolean enabled = Boolean.TRUE;

    @Size(max = 100)
    private String password;
}
