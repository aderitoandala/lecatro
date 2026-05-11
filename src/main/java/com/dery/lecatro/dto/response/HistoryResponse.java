package com.dery.lecatro.dto.response;

import com.dery.lecatro.entity.enums.HistoryEvent;
import java.time.LocalDateTime;

public record HistoryResponse(HistoryEvent event, String description, LocalDateTime occurredAt) {
}