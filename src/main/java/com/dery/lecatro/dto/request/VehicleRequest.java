package com.dery.lecatro.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehicleRequest(

    @NotBlank(message = "A marca é obrigatória")
    String brand,

    @NotBlank(message = "O modelo é obrigatório")
    String model,

    @NotBlank(message = "A cor é obrigatória")
    String color,

    @NotBlank(message = "O número de chassis é obrigatório")
    @Size(min = 17, max = 17, message = "O número de chassis deve ter exactamente 17 caracteres")
    @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}",
             message = "O número de chassis contém caracteres inválidos")
    String chassisNumber,

    @NotNull(message = "O ano de fabrico é obrigatório")
    @Min(value = 1900, message = "Ano inválido")
    Integer manufactureYear
) {}