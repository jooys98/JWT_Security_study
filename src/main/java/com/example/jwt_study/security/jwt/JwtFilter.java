package com.example.jwt_study.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor

//인증 요청 -> JwtFilter -> JWT 토큰 검증 -> CustomUserDetails -> 요청 처리
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    // 인증이 필요 없는 공개 API 경로인지 확인
    if (requestURI.startsWith("/api/posts/search") ||
            requestURI.startsWith("/api/posts/all") ||
            requestURI.startsWith("/api/posts/{id}") || // 필요에 따라 추가
            requestURI.startsWith("/api/posts/search/tag") ||
            requestURI.startsWith("/api/sub-categories") ||
            requestURI.startsWith("/api/main-categories")) {
      filterChain.doFilter(request, response); // 인증 없이 다음 필터로 진행
      return;
    }

    //1.  Authorization 헤더에서 JWT 토큰 추출
    String authToken = request.getHeader("Authorization");
    if (authToken == null || !authToken.startsWith("Bearer ")) {
      System.out.println("token null");
      filterChain.doFilter(request, response);
      return; // 헤더에 토큰이 없으면 return
    }

    //2. "Bearer" 제거하고 살제 토큰만 추출
    String token = authToken.substring(7);
    try { //3. 토큰 만료 여부 검사
      jwtUtil.isExpired(token);
    } catch (ExpiredJwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
      response.setCharacterEncoding("UTF-8");
      System.out.println("만료된 토큰");
      response.getWriter().write("만료된 토큰입니다.");
      return;
    }
    //4. 토큰 타임이 access 인지 검사
    //jwtUtil 에서 선언한 메서드 중 토큰의 종류(카테고리)를 설정하는 메서드를 호출하여 확인한다
    String category = jwtUtil.getCategory(token);
    if(!category.equals("access")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
      response.setCharacterEncoding("UTF-8");
      System.out.println("허용되지 않는 토큰");
      response.getWriter().write("허용되지 않는 토큰입니다.");
      return;
    }
    //5. jwtUtil 메서드중 username과 role을 설정하는 메서드로 유저네임과 권한을 추출함
    String username = jwtUtil.getUsername(token);
    String role = jwtUtil.getRole(token);

    if(!role.startsWith("ROLE_")) {
      role = "ROLE_" + role;
    } //  role 형식 검증

    // 6. Spring Security 인증&인가  처리를 위한 객체(배열) 생성
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role));

    // 인증된 사용자 정보 생성
    User user = new User(username, "", authorities);
    //인증된 사용자 정보 + 권한 정보를 포함한 Authentication 객체 생성
    Authentication auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
    // SecurityContext 에 인증 정보 저장
    SecurityContextHolder.getContext().setAuthentication(auth);
    filterChain.doFilter(request, response);
  }

}
