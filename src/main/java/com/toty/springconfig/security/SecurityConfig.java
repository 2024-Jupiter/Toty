package com.toty.springconfig.security;

import com.toty.springconfig.security.jwt.CustomAuthenticationEntryPoint;
import com.toty.springconfig.security.jwt.JwtRequestFilter;
import com.toty.springconfig.security.jwt.AccessTokenValidationFilter;
import com.toty.springconfig.security.oauth2.MyOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyOAuth2UserService myOAuth2UserService;

    private final SavedRequestAwareAuthenticationSuccessHandler formloginsuccess;
    private final JwtRequestFilter jwtRequestFilter;
    private final AccessTokenValidationFilter accessTokenValidationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(auth -> auth.disable())       // CSRF 방어 기능 비활성화
                .headers(x -> x.frameOptions(y -> y.disable()))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/posts/images/**", "/css/**", "/js/**", "/img/**", "/static/**").permitAll()
                        .requestMatchers("/view/posts/**", "/api/posts/**").permitAll()
                        // 테스트 엔드포인트
                        .requestMatchers("/api/users/test").hasAuthority("USER")
                        //멘토만 접근 가능한 url
//                        .requestMatchers(HttpMethod.POST, "").hasAuthority("MENTOR")
                        //관리자만 접근 가능한 url
//                        .requestMatchers("").hasAuthority("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(auth -> auth
                        .loginProcessingUrl("/api/users/sign-in")  // post 엔드포인트
                        .usernameParameter("email")
                        .passwordParameter("pwd")
                        .successHandler(formloginsuccess)
                        .failureHandler(loginFailureHandler())
                        .permitAll()
                )
                .logout(auth -> auth
                        .logoutUrl("/api/users/sign-out")
                        .deleteCookies("JSESSIONID")
                        .deleteCookies("accessToken")
                        .deleteCookies("refreshToken")
                        .logoutSuccessUrl("/view/users/alert/logout")
                )
                .oauth2Login(auth -> auth
                        .userInfoEndpoint(user -> user.userService(myOAuth2UserService))
                        .successHandler(formloginsuccess)
                        .failureHandler(loginFailureHandler())
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 토큰 관련 Filter 추가
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // 인증 경로 제외 모든 경로
        http.addFilterAfter(accessTokenValidationFilter, ExceptionTranslationFilter.class);

        return http.build();
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler loginFailureHandler() {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setDefaultFailureUrl("/view/users/alert/login-fail");
        return failureHandler;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/", "/js/", "/images/", "/static/");
    }
}
