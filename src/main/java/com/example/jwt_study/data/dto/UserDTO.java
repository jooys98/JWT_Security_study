package com.example.jwt_study.data.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

  private Long id;
  private String username;
  private String password;
  private String nickname;
  private String phoneNumber;
  private LocalDate joinDate;
  private String birthday;
  private UserRole role;

  public enum UserRole {
    ADMIN,
    USER
  }


}
