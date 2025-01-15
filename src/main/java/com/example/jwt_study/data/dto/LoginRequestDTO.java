package com.example.jwt_study.data.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//클라이언트에서 로그인한 유저의 정보를 안전하게 받아오기 위한 dto 객체 (일회성)
public class LoginRequestDTO {
    private String username;
    private String password;

}
