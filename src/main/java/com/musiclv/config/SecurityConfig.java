package com.musiclv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스와 미디어는 누구나
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/media/**", "/favicon.ico").permitAll()
                        // 오류 페이지까지 인증을 걸면 예외가 전부 로그인으로 튕겨 원인을 못 본다
                        .requestMatchers("/error").permitAll()
                        // 메인, 상품·공연 목록/상세/검색은 비회원도 볼 수 있다
                        .requestMatchers("/", "/products", "/products/**").permitAll()
                        .requestMatchers("/tickets", "/tickets/*").permitAll()
                        .requestMatchers("/about", "/guide").permitAll()
                        .requestMatchers("/members/signup", "/members/login").permitAll()
                        // 비회원 주문·주문조회. /orders/** 규칙보다 먼저 와야 한다.
                        .requestMatchers("/orders/guest", "/orders/guest/**").permitAll()
                        // 관리자 영역
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 장바구니/주문/예매/마이페이지는 로그인 필요
                        .requestMatchers("/cart/**", "/orders/**", "/bookings/**",
                                "/tickets/*/book", "/members/mypage").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/members/login")
                        .loginProcessingUrl("/members/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/members/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/members/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
