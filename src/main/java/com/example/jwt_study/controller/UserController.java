package com.example.jwt_study.controller;

import com.example.jwt_study.data.dto.UserDTO;
import com.example.jwt_study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")

//UserDTO(회원가입 정보) -> UserController -> Entity 변환 -> UserEntity 저장
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/id")
  public ResponseEntity<UserDTO> getUser(@RequestParam Long id) {

    return ResponseEntity.ok(userService.getUser(id));
  }
// ResponseEntity : HTTP 응답을 나타내는 클래스 (상태코드 , 헤더 , 본문 포함 할 수 있음)
//메서드 안 로직의 작동여부 응답 메세지 정확함
  @PostMapping("join")
  public ResponseEntity<String> joinUser(@RequestBody UserDTO userDTO) {
    String password = passwordEncoder.encode(userDTO.getPassword());
    //사용자가 입력한 비밀번호를 암호화
    userDTO.setPassword(password);
    //암호회된 비번을 dto에 저장
    userService.addUser(userDTO);
    //유저정보 dto 를 서비스 로직으로 보내서 저장
    return ResponseEntity.status(HttpStatus.CREATED).body("가입성공");
  }

  @GetMapping("/check")
  public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
    return ResponseEntity.ok(userService.existsByUsername(username));
  }
}
