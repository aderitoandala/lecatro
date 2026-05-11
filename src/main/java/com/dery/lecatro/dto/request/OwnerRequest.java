package com.dery.lecatro.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record OwnerRequest(

    @NotBlank(message = "O nome é obrigatório")
    String firstName,

    @NotBlank(message = "O apelido é obrigatório")
    String lastName,

    @NotBlank(message = "O NUIT é obrigatório")
    String nuit,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotNull(message = "A data de nascimento é obrigatória")
    LocalDate birthDate
) {}