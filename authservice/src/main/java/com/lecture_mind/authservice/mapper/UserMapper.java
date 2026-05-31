package com.lecture_mind.authservice.mapper;

import com.lecture_mind.authservice.model.User;
import com.lecturemind.commonservice.domain.Request.ReqLoginDTO;
import com.lecturemind.commonservice.domain.Response.ResLoginDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userLogin.id", source = "id")
    @Mapping(target = "userLogin.email", source = "email")
    @Mapping(target = "userLogin.fullName", source = "fullName")
    @Mapping(target = "userLogin.roles", expression = "java(user.getRoles().stream().map(role -> role.getName()).toList())",ignore = true)
    ResLoginDTO toLoginResponse(User user);

    ReqLoginDTO toLoginRequest(User user);
}
