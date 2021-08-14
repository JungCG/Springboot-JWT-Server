package com.cos.jwt.config.jwt;

public interface JwtProperties {
	String SECRET = "jcg"; // 우리 서버만 알고 있는 비밀 값
	int EXPIRATION_TIME = 60000*10;  // 60000 (1분) * 10 = 10분
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING = "Authorization";
}
