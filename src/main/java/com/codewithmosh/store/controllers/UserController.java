package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ChangePasswordDto;
import com.codewithmosh.store.dtos.RegisterUserRequests;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UsersDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.val;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @GetMapping
  public Iterable<UsersDto> getUsers(
      @RequestHeader(required = false, name = "x-auth-token") String authToken,
      @RequestParam(required = false, defaultValue = "", name = "sort") String sort) {
    if (!Set.of("name", "email", "password").contains(sort)) {
      sort = "name";
    }
    return userRepository.findAll(Sort.by(sort)).stream().map(userMapper::userToUserDto).toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<UsersDto> getUser(@PathVariable Long id) {
    var user = userRepository.findById(id).orElse(null);
    if (user == null) {
      return ResponseEntity.notFound().build();
    }

    val userDto = userMapper.userToUserDto(user);
    return ResponseEntity.ok(userDto);
  }

  @PostMapping
  public ResponseEntity<?> createUser(
      @Valid @RequestBody RegisterUserRequests registerUserRequests,
      UriComponentsBuilder uriBuilder) {
    if (userRepository.existsByEmail(registerUserRequests.getEmail())) {
      return ResponseEntity.badRequest().body(Map.of("email", "Email is already registered"));
    }

    var user = userMapper.toEntity(registerUserRequests);
    userRepository.save(user);

    var userDto = userMapper.userToUserDto(user);
    var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

    return ResponseEntity.created(uri).body(userDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UsersDto> updateUser(
      @PathVariable(name = "id") Long id, @RequestBody UpdateUserRequest registerUserRequests) {
    var user = userRepository.findById(id).orElse(null);
    if (user == null) {
      return ResponseEntity.notFound().build();
    }

    userMapper.update(registerUserRequests, user);
    userRepository.save(user);

    return ResponseEntity.ok(userMapper.userToUserDto(user));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
    var user = userRepository.findById(id).orElse(null);
    if (user == null) {
      return ResponseEntity.notFound().build();
    }
    userRepository.delete(user);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/change-password")
  public ResponseEntity<UsersDto> changePassword(
      @PathVariable Long id, @RequestBody ChangePasswordDto changePasswordDto) {
    var user = userRepository.findById(id).orElse(null);
    if (user == null) {
      return ResponseEntity.notFound().build();
    }

    if (!user.getPassword().equals(changePasswordDto.getOldPassword())) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    user.setPassword(changePasswordDto.getNewPassword());
    userRepository.save(user);
    return ResponseEntity.ok(userMapper.userToUserDto(user));
  }
}
