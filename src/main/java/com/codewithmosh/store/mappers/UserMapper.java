package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.RegisterUserRequests;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UsersDto;
import com.codewithmosh.store.entities.User;
import org.hibernate.sql.Update;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UsersDto userToUserDto(User user);

  User toEntity(RegisterUserRequests registerUserRequests);

  void update(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
