package com.example.jwt_study.security.jwt;

import com.example.jwt_study.data.entity.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 사용자 인증 정보를 담는 클래스
 * Spring Security의 UserDetails 인터페이스를 구현하여
 * 인증에 필요한 사용자 정보를 제공
 */

public class CustomUserDetails implements UserDetails {

  private final User user;
  //userEntity 에서 사용자 정보를 조회 후 검증하므로 User 엔티티를 참조함


  public CustomUserDetails(User user) {
    this.user = user;
  }
//유저 엔티티의 정보를 토대로 구현체(implement)인 UserDetails 형식으로 객체 생성

  /*userEntity 의 필드들을 조회하는 메서드들 */
  public Long getId() {
    return user.getId();
  }

  public String getNickname() {
    return user.getNickname();
  }

  public String getPhoneNumber() {
    return user.getPhoneNumber();
  }

  public LocalDate getJoinDate() { return user.getJoinDate();}


  public String getRole() {
    return user.getRole().name();
  }

  @Override // 사용자의 권한 정보 반환 (user , admin)
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getRole() == User.UserRole.ADMIN
        ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        : List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  //계정 만료 기능 사용 안함
  @Override
  public boolean isAccountNonExpired() {
    return true; // 만료 안됐다는 뜻
  }

  //계정잠금기능 사용 안함
  @Override
  public boolean isAccountNonLocked() {
    return true; //잠금 열려있다는 뜻
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // 비밀번호 만료 기능 사용 안함
  }

  @Override
  public boolean isEnabled() {
    return true; // 계정 비활성화 기능을 사용하지 않음
  }
}
