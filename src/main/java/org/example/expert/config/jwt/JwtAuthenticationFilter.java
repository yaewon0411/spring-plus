package org.example.expert.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.security.handler.SecurityResponseHandler;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper om;
    private final SecurityResponseHandler securityResponseHandler;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ObjectMapper om, SecurityResponseHandler securityResponseHandler) {
        super(authenticationManager);
        setFilterProcessesUrl("/auth/signin");

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.om = om;
        this.securityResponseHandler = securityResponseHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            SigninRequest signinRequest = om.readValue(request.getInputStream(), SigninRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            signinRequest.getEmail(),
                            signinRequest.getPassword());
            return authenticationManager.authenticate(authenticationToken);
        }catch (Exception e){
            log.info(e.getMessage(), e);
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String token = jwtUtil.createToken(loginUser.getUser());
        SigninResponse signinResponse = new SigninResponse(loginUser.getUser());

        response.addHeader(JwtUtil.HEADER, token);
        securityResponseHandler.success(response, signinResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        securityResponseHandler.fail(response, "로그인에 실패했습니다", HttpStatus.UNAUTHORIZED);
    }
}
