package com.dery.lecatro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(

    @NotBlank(message = "A marca é obrigatória")
    String brand,

    @NotBlank(message = "O modelo é obrigatório")
    String model,

    @NotBlank(message = "A cor é obrigatória")
    String color,

    @NotBlank(message = "O número de chassis é obrigatório")
    String chassisNumber,

    @NotNull(message = "O ano de fabrico é obrigatório")
    Integer manufactureYear
) {}