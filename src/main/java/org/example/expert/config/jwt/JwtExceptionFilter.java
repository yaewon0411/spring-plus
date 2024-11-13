package org.example.expert.config.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.security.handler.SecurityResponseHandler;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final SecurityResponseHandler securityResponseHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (InvalidRequestException e){
            log.error("jwtExceptionFilter - 인가 오류 발생: {}", e.getMessage(), e);
            securityResponseHandler.fail(response, e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(ServerException e){
            log.error("jwtExceptionFilter - 서버 및 내부 오류 발생: {}", e.getMessage(), e);
            securityResponseHandler.fail(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
