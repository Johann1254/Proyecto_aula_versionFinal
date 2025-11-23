package com.example.demo.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
                                        throws IOException, ServletException {

        String errorMessage = "Tu usuario está inactivo. Contacta al administrador";

        if (exception instanceof DisabledException) {
            errorMessage = "Tu usuario esta inactivo. Contacta al administrador.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Usuario o contraseña incorrectos.";
        }

        // Redirige al login con el mensaje como parámetro
        response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }
}