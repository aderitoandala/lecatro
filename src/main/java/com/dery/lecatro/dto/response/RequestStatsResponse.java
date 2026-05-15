package com.dery.lecatro.dto.response;

public record RequestStatsResponse(
    int year,               // ano consultado
    Integer month,          // mês consultado (null se for consulta anual)
    long totalRequests,     // total de pedidos no período
    long totalPending,      // pedidos pendentes
    long totalPaid,         // pedidos pagos
    long totalIssued,       // pedidos com matrícula emitida
    long totalCancelled     // pedidos cancelados
) {}