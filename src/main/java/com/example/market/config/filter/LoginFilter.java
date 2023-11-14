package com.example.market.config.filter;

import com.example.market.api.ApiResponse;
import com.example.market.domain.entity.user.PrincipalUserDetails;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.user.request.UserLoginRequest;
import com.example.market.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
//            User user = objectMapper.readValue(request.getInputStream(), User.class);
            UserLoginRequest user = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            log.error("{}", e);
        }
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalUserDetails userDetails = (PrincipalUserDetails) authResult.getPrincipal();
        String token = tokenProvider.createAccessToken(userDetails.getUser());
        // TODO: refreshToken 으로 변경
//        ResponseCookie accessToken = ResponseCookie
//                .from("accessToken", token)
//                .path("/")
//                .httpOnly(true)
//                .maxAge(3600)
//                .sameSite("Lax")
//                .build();
//        response.addHeader("Set-Cookie", accessToken.toString());
        setTokenResponse(response, token);
    }

//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        super.unsuccessfulAuthentication(request, response, failed);
//    }

    private void setTokenResponse(HttpServletResponse response, String accessToken) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);

        response.getWriter().println(
                objectMapper.writeValueAsString(
                        ApiResponse.ok(result)));
//        response.getWriter().println(
//                objectMapper.writeValueAsString(
//                        TokenResponse.authResponse(HttpServletResponse.SC_OK, result)));
    }
}