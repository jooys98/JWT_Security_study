# JWT_Security_study


#Spring Security + JWT 토큰 방식을 활용하여 로그인 , 로그아웃 , 회원가입 구현 (하드코딩)

😳보안은 너무 어려워요잉 <login 흐름>
1. 클라이언트의 로그인 요청
    * username과 password를 담아서 request 전송
2. LoginFilter
    * request(json) 에서 loginRequestDTO (java객체)로 변환 (ObjectMapper)
    * 자바객체로 변환한 정보를 UsernamePasswordAuthenticationToken 필터 양식에 맞게 바꾸고
    * 이 정보를 UsernamePasswordAuthenticationToken 객체로 변환
    * AuthenticationManager에게 인증 처리 위임
3. AuthenticationManager
    * CustomUserDetails를 통해 DB에서 사용자 정보 조회
    * 비밀번호 검증
    * 인증 성공시 Authentication 객체 반환
4. LoginFilter의 successfulAuthentication 메서드
    * Authentication 객체에서 CustomUserDetails 정보 추출
    * JwtUtil을 사용해서 JWT 토큰 생성
        * access 토큰 (1시간)
        * refresh 토큰 (24시간)
    * 사용자 정보를 Map에 담고 JSON으로 변환
    * response에 토큰과 JSON 데이터를 담아서 클라이언트에게 반환 이후 클라이언트는 받은 토큰을:
* access 토큰: Authorization 헤더에 담아서 요청
* refresh 토큰: 쿠키에 저장하여 access 토큰 갱신에 사용
<logout 흐름>
1. 클라이언트의 로그아웃 요청
* /logout 엔드포인트로 POST 요청 전송 (SecurityConfig 클래스)
2. Spring Security의 로그아웃 필터
* 요청을 가로채서 로그아웃 처리 시작
* logoutUrl("/logout")으로 설정된 URL에서 동작
3. LogoutHandler 실행
* 세션이 존재하면 세션 무효화
4. 인증에 관련된 쿠키 삭제
5. LogoutSuccessHandler 실행
* 클라이언트에 성공 응답 전송

Request : 
["Content-Type"] = "application/x-www-form-urlencoded" 에서 json 으로 수정 

😀notion 링크 -> https://whispering-shoemaker-e1d.notion.site/Spring-Security-JWT-17a45e7562a580e98974c0a46935b33f?pvs=4
