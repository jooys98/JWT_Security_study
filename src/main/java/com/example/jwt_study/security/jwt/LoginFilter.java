package com.example.jwt_study.security.jwt;

import com.example.jwt_study.data.dto.LoginRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//사용자가 로그인 요청을 보내면 요청을 여기서 가로채서 인증처리를 함
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    //UsernamePasswordAuthenticationFilter : 스프링시큐리티에서 기본적으로 제공하는 로그인 필터 클래스
    // 사용자의 이름 비번을 추출하여 이를 기반으로 인증을 시도(request -> username , password)
    //이를 상속 받은 이유
    //기본 로그인 처리 로직을 재사용
//JWT 토큰 발급 등 추가 기능을 구현하기 위해
//커스텀한 인증 성공/실패 핸들러를 구현하기 위해
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;
    //JwtUtil : JWT 토큰 생성, 검증, 파싱을 담당하는 유틸리티 클래스
    //AuthenticationManager  : 스프링시큐리티의 핵심 인터페이스 , 인증 처리의 실제 작업 , 아이디 비번 유효성을 검증
    //성공시 Authentication 객체 리턴
    //실패시 AuthenticationException 리턴


    //request: 클라이언트 → 서버 요청
    //response: 서버 → 클라이언트 응답

    @Override
    //로그인 인증을 처리하는 메서드
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        //HttpServletRequest request : http 요청 객체
        //HttpServletResponse response : 서버의 응답 객체
        //throws AuthenticationException : 인증 실패시 예외처리

//    String username = obtainUsername(request); //클라이언트에서 받은 request 애서 username 과 pw를 추출
//    String password = obtainPassword(request); //obtain : UsernamePasswordAuthenticationFilter 에서 제공하는 메서드

//    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
//        username, password, null);
        // 추출한 username 과 pw을 AuthenticationManager 가 인증할 수 있는 형태로 만들어 인증을 시도한다
        //마지막 값이 null 인 이유는 아직 jwt 토큰이 발급되지 않았기 때문

//    return authenticationManager.authenticate(authRequest);//인증결과를 리턴  ,여기서 성공시 Authentication , 실패시 AuthenticationException 를 반환

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            //ObjectMapper: Jackson 라이브러리에서 제공하는 클래스
            LoginRequestDTO loginRequestDTO = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);
            //클라이언트에서 받은 json 객체-> LoginRequestDTO 값에 매핑하여 자바 객체 로 바꿔줌
            // loginRequestDTO.username = "wndbstn"
            // loginRequestDTO.password = "1111"
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    loginRequestDTO.getUsername(),loginRequestDTO.getPassword(), null);
            // getter 를 사용하여 값을 가져옴 , null 은 토큰 자리

            return authenticationManager.authenticate(authRequest);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    //인증 성공시(Authentication 반환) 처리 하는 메서드
    //사용자 정보를 바탕으로 토큰 생성 , 응답 보냄
    public void successfulAuthentication(HttpServletRequest request, // 클라의 요청
                                         HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException {
        //서버의 응답  // 로그인 필터  // 인증객체 // 실패시 예외처리


        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
//해당 유저 정보를 CustomUserDetails 객체로 만든다

        // CustomUserDetails 에서 필요한 사용자 정보 추출
        String userId = String.valueOf(userDetails.getId());
        String username = userDetails.getUsername();
        //사용자의 권한 정보는 string 으로 변환
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());
//권한확인 admin 인지 user인지 ..?
        LocalDate joinDate = userDetails.getJoinDate();

        /**
         * accessToken과 refresh 쿠키를 생성
         */
        String accessToken = this.jwtUtil.CreateJWT("access", username, role, 60 * 60 * 1000L); //엑세스토큰 : 1시간 짜리
        String refreshToken = this.jwtUtil.CreateJWT("refresh", username, role, 24 * 60 * 60 * 1000L); // 리프레쉬토큰 : 24시간


        Map<String, Object> responseData = new HashMap<>();
        responseData.put("user_id", userId);
        responseData.put("username", username);
        responseData.put("nickname", userDetails.getNickname());
        responseData.put("phone_number", userDetails.getPhoneNumber());
        responseData.put("joinDate", joinDate);
        responseData.put("role", role);
        //클라에 보낼 리스펀스에 담길 유저의 정보를 hashMap 형태의 배열로 감싼다

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonMessage = objectMapper.writeValueAsString(responseData);
        //map에 저장된 responseData 를 제이슨 형태로 변환한다

        response.addHeader("Authorization", "Bearer " + accessToken);
        // 헤더에 Authorization 이게 있으면 인증처리가 되어 로그인 성공 , Bearer 는 jwt 토큰임을 나타내는 접두어임
        response.addCookie(createCookie("refresh", refreshToken));
        // 리프레쉬 토큰은 쿠키에 담아져서 전달됨
        // 쿠키 :서버에 데이터를 안전하게? 전달해주는 브라우저의 요소 중 하나
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(jsonMessage);
    }

    /**
     * createCookie 만드는 메서드
     * cookie 형태 = (key, value)
     */
    //쿠키 생성 메서드
    private Cookie createCookie(String key, // 쿠키 이름
                                String value) // 쿠키의 값 ( 쿠키 속 리프레쉬 토큰)
    {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/"); // 모든 경로에서 발생
        cookie.setHttpOnly(true); // 자바스크립트 접근 불가
        cookie.setMaxAge(24 * 60 * 60); // 쿠키의 유효시간
        return cookie;
    }

    @Override
    //인증 실패시 처리하는 메서드
    //AuthenticationException 반환 시
    public void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res,
                                           AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "계정정보가 틀립니다.");
        //인증에 실패한 놈들을 또 map 으로 감싸고 인증이 안됐다는 메세지를 넣는다

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonmessage = objectMapper.writeValueAsString(responseData);
        // 인증 실패한 놈들 + 메세지 를 제이슨으로 바꿔서 클라에 보낼 준비
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jsonmessage);
    }
}
