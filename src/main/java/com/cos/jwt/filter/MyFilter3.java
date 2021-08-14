package com.cos.jwt.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.jwt.config.jwt.JwtProperties;

// 테스트 용도
public class MyFilter3 implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// 테스트 토큰 : jcg
		// ID, PW 정상적으로 들어와서 로그인이 완료되면
		// 토큰을 만들어주고 해당 토큰을 응답해준다.
		// 요청할 때 마다 header에 Authorizaion에 value 값으로 토큰을 가지고 오고,
		// 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증하면 됨. (RSQ, HS256)
		if (req.getMethod().equals("POST")) {
			System.out.println("POST 요청됨");
			String headerAuth = req.getHeader(JwtProperties.HEADER_STRING);
			System.out.println(headerAuth);
			System.out.println("필터3");

			PrintWriter out = res.getWriter();
			if (headerAuth != null && headerAuth.equals(JwtProperties.SECRET)) {
				// headerAuth.equals(JwtProperties.SECRET) 수정해서 추가 구현 시 사용
				out.println("이미 인증된 사용자입니다.");
			} else {
				out.println("아직 인증처리가 되지 않은 사용자입니다.");
			}
			// 다시 chain에 넘겨줘야 한다.
			chain.doFilter(req, res);
		}
	}
}
