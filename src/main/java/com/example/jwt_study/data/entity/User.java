package com.example.jwt_study.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tbl")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @NotEmpty(message = "비밀번호는 비어있을 수 없습니다.")
  @Size(max = 255)
  private String password;

  @Size(max = 255)
  @Column(name = "username")
  private String username;

  @Size(min = 1, max = 10)
  private String nickname;

  @Size(max = 11)
  private String phoneNumber;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate joinDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  public enum UserRole {
    ADMIN,
    USER
  }


}
