package com.cos.jwt.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// @CrossOrigin : 필터를 사용하지 않고 @CrossOrigin을 사용하면
// 인증이 필요한 요청은 다 거부됨
// -> SecurityConfig 에 .addFilter(corsFilter) 로 필터를 걸어줘야한다.
@RestController
@RequiredArgsConstructor
public class RestApiController {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/home")
	public String home() {
		return "<h1>home</h1>";
	}
	
	@PostMapping("/token")
	public String token() {
		return "<h1>token</h1>";
	}
	
	@PostMapping("/join")
	public String join(@RequestBody User user) {
		user.setRoles("ROLE_USER");
//		userRepository.save(user); // 회원가입이 되지만 비밀번호 : 1234 => 시큐리티로 로그인할 수 없음. 이유는 패스워드가 암호화가 안되었기 때문에
		
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);
		
		return "회원가입 성공";
	}
	
	// user, manager, admin 권한만 접근 가능
	@GetMapping("/api/v1/user")
	public String user() {
		return "user";
	}
	
	// manager, admin 권한만 접근 가능
	@GetMapping("/api/v1/manager")
	public String manager() {
		return "manager";
	}
	
	// admin 권한만 접근 가능
	@GetMapping("/api/v1/admin")
	public String admin() {
		return "admin";
	}
}
