package org.example.expert.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.security.dto.ErrorResponse;
import org.example.expert.exception.ServerException;
import org.example.expert.util.api.ApiError;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class SecurityResponseHandler {

    private final ObjectMapper om;

    public void fail(HttpServletResponse response, String msg, HttpStatus httpStatus){
        try {
            ApiResult<ApiError> error = ApiResult.Companion.error(httpStatus.value(), msg);
            String responseBody = om.writeValueAsString(error);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        }catch (Exception e){
            log.error("응답 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new ServerException("서버 내부 오류가 발생했습니다");
        }
    }

    public void success(HttpServletResponse response, Object obj){
        try{
            String responseBody = om.writeValueAsString(ApiResult.Companion.success(obj));
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        }catch (Exception e){
            log.error("응답 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new ServerException("서버 내부 오류가 발생헀습니다");
        }
    }
}
