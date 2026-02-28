package com.sq.system.common.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtil {

    public static void setLoginCookies(HttpServletResponse response, String token, Long projectId) {
        ResponseCookie tokenCookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .secure(true)
                .sameSite("None")
                .build();

        ResponseCookie projectCookie = ResponseCookie.from("projectId", String.valueOf(projectId))
                .httpOnly(false)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, projectCookie.toString());
    }

    public static void clearLoginCookies(HttpServletResponse response) {
        ResponseCookie tokenCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .build();

        ResponseCookie projectCookie = ResponseCookie.from("projectId", "")
                .httpOnly(false)
                .path("/")
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, projectCookie.toString());
    }
}
