package com.cos.jwt.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
// /login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 동작
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;

	// /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter : 로그인 시도중");

		try {
//			BufferedReader br = request.getReader();
//			
//			String input = null;
//			while((input = br.readLine()) != null) {
//				System.out.println(input);
//			}
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(), User.class);
			System.out.println(user);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					user.getUsername(), user.getPassword());

			// PrincipalDetailsService 의 loadUserByUsername 함수가 실행된 후 정상이면 authentication이 리턴됨.
			// DB에 있는 username과 password 가 일치한다.
			Authentication authentication = authenticationManager.authenticate(authenticationToken);

			// => 로그인이 되었다는 뜻.
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			System.out.println("로그인 완료 : "+principalDetails.getUser().getUsername()); // 로그인이 정상적으로 되었다는 뜻.

			//	authentication 객체를 session 영역에 저장을 해야하고 그 방법이 RETURN 해두면 된다.
			// 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는 것.
			// 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없음. 단지 권한 처리 때문에 session에 넣어줌.
			return authentication;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행된다.
	// 여기서 JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 된다.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 실행됨 : 인증이 완료되었다는 뜻");
		
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		
		// RSA 방식은 아니고 Hash 암호 방식
		String jwtToken = JWT.create()
				.withSubject("JCG토큰")
				.withExpiresAt(new Date(System.currentTimeMillis()+(JwtProperties.EXPIRATION_TIME)))
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET)); // 서버만 알고 있는 secret 키
		
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
		
	}
}
