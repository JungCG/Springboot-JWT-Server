package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.config.jwt.JwtAuthorizationFilter;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final CorsFilter corsFilter;
	private final UserRepository userRepository;
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Security Filter Chain에 걸때
		// Security Filter Chain 이 우리가 만든 MyFilter보다 먼저 동작한다.
		// Security Filter Chain의 첫번째 필터 : SecurityContextPersistenceFilter
		// 아래 MyFilter3는 Security Filter Chain 보다 먼저 실행
//		http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
		
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다, Stateless 서버로 만들겠다.
		// 기본적으로 Web은 Stateless -> Stateful 처럼 쓰기위해 세션과 쿠키 사용 
		.and()
		.addFilter(corsFilter) // @CrossOrigin(인증 X), 시큐리티 필터에 등록 인증 O
		.formLogin().disable() // JWT 서버이기 때문에 ID, PWD를 사용하는 Form tag Login을 안한다.
		// headers 안에 Authorization 에 ID, PW 를 요청때마다 담아서 전송하는 것이 Http Basic 방식 -> 서버 확장성에서는 좋으나 노출 가능성
		// -> Authorization 안에 Token을 담는다. ID, PW 가 직접 노출될 일은 없다 -> Bearer 방식
		// Token은 유효시간을 가지고 있음
		.httpBasic().disable() // 기본적인 http 인증 방식은 사용 안한다.
		.addFilter(new JwtAuthenticationFilter(authenticationManager())) // AuthenticationManager 을 줘야한다.
		.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
		.authorizeRequests()
		.antMatchers("/api/v1/user/**")
		.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
		.antMatchers("/api/v1/manager/**")
		.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
		.antMatchers("/api/v1/admin/**")
		.access("hasRole('ROLE_ADMIN')")
		.anyRequest().permitAll();
	}
}
