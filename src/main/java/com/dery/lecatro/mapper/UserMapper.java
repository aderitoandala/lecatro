package com.dery.lecatro.mapper;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.dto.response.UserResponse;
import com.dery.lecatro.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "publicId", ignore = true)
	@Mapping(target = "password", ignore = true)
	User toEntity(UserRequest request);

	UserResponse toResponse(User user);
}
