package org.example.expert.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.security.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityResponseHandler {

    private final ObjectMapper om;

    public void fail(HttpServletResponse response, String msg, HttpStatus httpStatus){
        try {

            ErrorResponse errorResponse = ErrorResponse.of(msg, httpStatus.value());
            String responseBody = om.writeValueAsString(errorResponse);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        }catch (Exception e){
            log.error("서버 파싱 에러");
        }
    }
}
