package com.dery.lecatro.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RequestRequest(

    @NotNull(message = "O proprietário é obrigatório")
    UUID ownerPublicId,     

    @NotNull(message = "O veículo é obrigatório")
    UUID vehiclePublicId  
) {}