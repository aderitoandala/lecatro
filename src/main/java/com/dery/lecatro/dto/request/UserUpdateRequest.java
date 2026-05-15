package com.dery.lecatro.dto.request;

import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
		
	@NotBlank(message = "O email é obrigatório")
	@Email(message = "Email inválido")
    String email,

    String password, 

    @NotNull(message = "A província é obrigatória")
    Province province,
    
    @NotNull(message = "A função é obrigatória")
    Role role
) {}