package com.example.userservice.security;

import com.example.userservice.Service.UserService;
import jakarta.servlet.Filter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.Set;
import java.util.function.Supplier;


@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityNew {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public static final String ALLOWED_IP_ADDRESS = "127.0.0.1";
    public static final String SUBNET = "/32";
    public static final IpAddressMatcher ALLOWED_IP_ADDRESS_MATCHER = new IpAddressMatcher(ALLOWED_IP_ADDRESS + SUBNET);

    public WebSecurityNew(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception
    {// 권한 관련 메소드
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.csrf( (csrf) -> csrf.disable());

        http.authorizeHttpRequests((authz) -> authz
                                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/welcome")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/health-check")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
//                        .requestMatchers("/**").access(this::hasIpAddress)
//                                .requestMatchers("/**").access(
//                                        new WebExpressionAuthorizationManager(
//                                                "hasIpAddress('127.0.0.1') " +
//                                                "or hasIpAddress('192.168.45.83')"+
//                                                "or hasIpAddress('172.17.0.0/32')")) // host pc ip address
//                                .anyRequest().authenticated()
                // IP 주소 체크와 로그 찍기
                            .requestMatchers("/**").access((authentication, httpServletRequest) -> {
                                    // 클라이언트 IP 주소 가져오기
                                    String clientIp = httpServletRequest.getRequest().getRemoteAddr();
                                    log.info("Request from IP: {}", clientIp);  // 로그 출력

                                    // IP 주소가 허용된 범위 내인지 확인
                                    if (clientIp.equals("127.0.0.1") || clientIp.equals("192.168.45.83") || clientIp.startsWith("172.18.")) {
                                        log.info("Access granted for IP: {}", clientIp);  // 허용된 IP에 대한 로그
                                        return new AuthorizationDecision(true);  // 인증 통과
                                    } else {
                                        log.warn("Access denied for IP: {}", clientIp);  // 허용되지 않은 IP에 대한 로그
                                        return new AuthorizationDecision(false);  // 인증 실패
                                    }
                                })
                                .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .authenticationManager(authenticationManager);
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilter(getAuthenticationFilter(authenticationManager));
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));

        return http.build();
    }
    private AuthorizationDecision hasHostNameOrIp(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String remoteHost = context.getRequest().getRemoteHost();
        String remoteAddr = context.getRequest().getRemoteAddr();

        // 허용할 IP 주소
        Set<String> allowedIps = Set.of(
                "127.0.0.1", // 로컬 호스트
                "192.168.45.83", // 허용할 로컬 네트워크 IP
                "172.18.0.0/16" // Docker 네트워크 서브넷 (예시: Docker의 기본 bridge 네트워크 범위)
        );
        boolean isAllowed = "localhost".equalsIgnoreCase(remoteHost) || allowedIps.contains(remoteAddr);

        return new AuthorizationDecision(isAllowed);
    }


    private AuthorizationDecision hasIpAddress(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        return new AuthorizationDecision(ALLOWED_IP_ADDRESS_MATCHER.matches(object.getRequest()));
    }


    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        return new AuthenticationFilter(authenticationManager, userService, env);
    }

}
