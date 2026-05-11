package com.dery.lecatro.mapper;

import org.mapstruct.Mapper;

import com.dery.lecatro.dto.response.HistoryResponse;
import com.dery.lecatro.entity.History;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

	HistoryResponse toResponse(History history);
}