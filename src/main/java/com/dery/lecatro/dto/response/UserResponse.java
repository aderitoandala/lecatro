package com.dery.lecatro.dto.response;

import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.Role;
import java.util.UUID;

public record UserResponse(UUID publicId, String email, Province province, Role role) {
}