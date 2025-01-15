package com.example.jwt_study.service;

import com.example.jwt_study.data.dto.UserDTO;
import com.example.jwt_study.data.entity.User;

import java.time.LocalDate;

public interface UserService {

    UserDTO getUser(Long id);

    void addUser(UserDTO userDTO);

    boolean existsByUsername(String username);

    //entity -> dto
    default UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .birthday(String.valueOf(user.getBirthday()))
                .build();
    }

    //dto -> entity
    default User toEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .nickname(userDTO.getNickname())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .phoneNumber(userDTO.getPhoneNumber())
                .birthday(LocalDate.parse(userDTO.getBirthday()))
                .build();
    }
}
