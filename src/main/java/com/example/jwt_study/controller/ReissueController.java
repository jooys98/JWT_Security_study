package com.example.jwt_study.controller;

import com.example.jwt_study.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

private final JwtUtil jwtUtil;
//새 토큰을 만들어주는 컨트롤러
    @PostMapping(value = "/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null; // refresh null 로 초기화
        Cookie[] cookies = request.getCookies();
        //http 의 request(요청)에 있는 쿠키 배열 속 쿠키 를 가져온다
        if(cookies == null || cookies.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠키가 없습니다"); // 400
        } //쿠키의 벨류 값이  null 일떄 처리

        for (Cookie cookie : cookies) { //쿠키 배열의 모든 쿠키를 하나씩 확인
            if (cookie.getName().equals("refresh")) { //쿠키의 이름이 refresh 인지 확인하고 이게 true 일시
                refresh = cookie.getValue(); //쿠키의 벨류 값을 refresh 에 넣는다
                break;
            }
        }

        if(refresh == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("refresh 토큰 null"); // 400
        }

        try {
            this.jwtUtil.isExpired(refresh); //jwtUtil 에서 만든 토큰의 만료 확인 메서드를 호출 하여 토큰의 만료 여부를 확인한다
        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh 토큰 유효기간 만료"); // 401
        }

//        String category = jwtUtil.getCategory(refresh);
//        if(!category.equals("refresh")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 refresh 토큰"); // 400
//        }

        String username = jwtUtil.getUsername(refresh);
        // 찾은 리프레쉬 토큰 값으로 jwtUtil 에서 만든 유저네임을 확인하는 메서드를 호출하여 username 을 뽑아내어 저장
        String role = jwtUtil.getRole(refresh);
        // 권한정보도 뽑아내어 저장한다
        String newAccessToken = jwtUtil.CreateJWT("access", username, role, 60*60*1000L);
        response.addHeader("Authorization", "Bearer " + newAccessToken);
        response.setCharacterEncoding("UTF-8");
        return ResponseEntity.status(HttpStatus.OK).body("새 토큰 발급 성공");
//뽑아낸 정보를 토대로 jwtUtil 의 토큰 생성 메서드를 호출하여 새 토큰을 만든다
    }
}
