package com.dery.lecatro.dto.request;

import com.dery.lecatro.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(

    @NotNull(message = "O pedido é obrigatório")
    UUID requestPublicId,   

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    BigDecimal amount,

    @NotNull(message = "O método de pagamento é obrigatório")
    PaymentMethod method
) {}