package com.dery.lecatro.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OwnerRequest(

    @NotBlank(message = "O nome é obrigatório")
    String firstName,

    @NotBlank(message = "O apelido é obrigatório")
    String lastName,

    @NotBlank(message = "O NUIT é obrigatório")
    @Pattern(regexp = "\\d{9}", message = "O NUIT deve conter exactamente 9 algarismos")
    String nuit,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotNull(message = "A data de nascimento é obrigatória")
    LocalDate birthDate
) {}