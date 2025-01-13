package com.example.jwt_study.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
 //인증 성공 시 여기서 jwt 토큰이 만들어짐
  private SecretKey secretKey;

  //이 클래스에서 과정 1번
  public JwtUtil(@Value("${app.props.jwt.secret.key}") String secretKeyString) {
      //yml에 만들어놓은 문자열을 가지고 와서 이걸 토큰을 만드는데 사용할 시크릿 키로 변환
      // 시크릿키는 토큰을 만들어줄때와 토큰을 검증할 때 쓰이게 됨
    System.out.println("Loaded secret key: " + secretKeyString); // 디버깅 메시지

    if (secretKeyString == null || secretKeyString.isEmpty()) {
      throw new IllegalArgumentException("JWT secret key cannot be empty");
    } //시크릿키가 없을때 예외처리
    byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
    //secretKeyString 를 바이트 문자열 배열로  변환 -> secretKey 로 만들어줌
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }


//사용자의 요펑에 맞게 이 메서드들로 토큰에서 정보를 추출하여 확인한다
    //과정 3번
  public String getCategory(String token) {
      //시크릿 키 에서 토큰의 종류를 추출함 ( access 인지 refresh 인지 )
    return Jwts.parser().verifyWith(this.secretKey).build()
        .parseSignedClaims(token).getPayload()
        .get("category", String.class);
  }

  public String getUsername(String token) {
      //시크릿 키 에서 사용자 이름 추출
    return Jwts.parser().verifyWith(this.secretKey).build()
        .parseSignedClaims(token).getPayload()
        .get("username", String.class);
  }

  public String getRole(String token) {
    return Jwts.parser().verifyWith(this.secretKey).build()
        .parseSignedClaims(token).getPayload()
        .get("role", String.class);
  }

  public Boolean isExpired(String token) {
    return Jwts.parser().verifyWith(this.secretKey).build()
        .parseSignedClaims(token).getPayload()
        .getExpiration().before(new Date());
  } // 토큰의 만료여부 확인


    //로그인 (인증) 성공시 여기서 토큰이 만들어짐 과정 2번
  public String CreateJWT(String category, String username, String role, Long expireMs) {
    return Jwts.builder()
        .claim("category", category)
        .claim("username", username)
        .claim("role", role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expireMs))
        .signWith(this.secretKey)
        .compact();
  }
}
