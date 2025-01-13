package com.example.jwt_study.service;

import com.example.jwt_study.data.dto.UserDTO;
import com.example.jwt_study.data.entity.User;
import com.example.jwt_study.data.entity.User.UserRole;
import com.example.jwt_study.data.repository.UserRepository;
import com.example.jwt_study.security.jwt.CustomUserDetails;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service //서비스 계층 컴포넌트
public class UserServiceImpl implements UserService, UserDetailsService {

  private final UserRepository userRepository; // 데이터 접근을 위해 repository 가져옴

  @Transactional(readOnly = true) //읽기 전용 트랜잭션
  @Override
  public UserDTO getUser(Long id) {
//id 로 entity 에서 사용자 정보 조회 -> dto로 변환시켜 반환
    User user = userRepository.findById(id).orElse(null);
    //유저엔티티에서 조회한 유저 정보를 user 객테에 저장하고 유저가 없을 경우엔 null 을 반환하게 함
    return user != null ? toDTO(user) : null;
  } //삼항 연산자 user 가 null 이 아니면 → DTO 로 변환해서 반환
  //user 가 null 이면 → null 반환

  @Transactional
  @Override
  public void addUser(UserDTO userDTO) {
//새로운 유저 등록
    String username = userDTO.getUsername(); //사용자먕 중복체크
    Optional<User> existingUser = userRepository.findByUsername((username));
    if (existingUser.isPresent()) {
      throw new RuntimeException("이미 존재하는 사용자명입니다.");
    }
//새로운 유저 정보 엔티티 생성 , 저장
    User user = toEntity(userDTO);
    user.setJoinDate(LocalDate.now());
    user.setRole(UserRole.USER);
    User saveUser = userRepository.save(user);
    toDTO(saveUser);
  }

  @Transactional(readOnly = true)
  @Override //username 이 존재하는지 확인하는 boolean 메서드
  public boolean existsByUsername(String username) {
    return userRepository.findByUsername((username)).isPresent();
  }

  @Transactional
  @Override
  //로그인 시 사용자의 정보를 로드함
  //스프링시큐리티를 위한 사용자 정보 read 메서드
  // CustomUserDetails 객체로 반환하여 시큐리티가 해당 유저의 정보를 읽을 수 있게 해줌
  //UserDetails -> CustomUserDetails 클래스가 상속 받은 시큐리티 클래스
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isEmpty()) {
      throw new UsernameNotFoundException("User " + username + " not found");
    }
    return new CustomUserDetails(userOptional.get());
  }
}
