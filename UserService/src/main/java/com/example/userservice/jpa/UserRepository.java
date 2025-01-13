package com.example.userservice.jpa;

import com.example.userservice.dto.UserDto;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findAllByEmail(String username);
    Optional<UserEntity> findByEmail(String email);
}
