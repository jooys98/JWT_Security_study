package com.example.jwt_study.data.repository;

import com.example.jwt_study.data.entity.User;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("select u from User u where u.username= :username")
  //메서드 파라미터 이름과 JPQL의 :username이 일치하면 @Param 생략 가능
  //쿼리에 있는 username 은 실제 엔티티에 존재하는 username 이고
  //파라미터 이름이랑 일치하는 경우 @Param  생략 가능
  Optional<User> findByUsername(@Size(max = 255) String username);
//Optional : null 처리를 명확하게 하여 데이터의 존배 여부를 확실하게 알 수 있음
  //엔티티에 파라미터로 받은 유저네임에 해당하는 정보들을 조회하고 반환

  @Query("SELECT u.id FROM User u WHERE u.username = :username")
  Optional<Long> findUserIdByUsername(@Param("username") String username);
//엔티티에 파라미터로 받은 username 에 해당하는 아이디를 조회 후  반환

  @Query("SELECT u.username FROM User u WHERE u.id = :id")
  Optional<String> findByUserId(@Param("id") Long id);
//엔티티에 파리미터로 받은 아이디로 해당 유저 네임을 조회 후 반환
}

