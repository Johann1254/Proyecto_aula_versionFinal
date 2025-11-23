package com.example.demo.config;

import com.example.demo.security.CustomAuthenticationFailureHandler;
import com.example.demo.security.CustomAuthenticationSuccessHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired  
    private CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/", "/landing", "/login", "/css/**", "/js/**", "/img/**", "/scss/**",
                                "/vendor/**")
                        .permitAll()

                        // Rutas privadas
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/home/**").hasRole("ADMIN")
                        .requestMatchers("/home_u/**", "/usuario/**").hasRole("USER")
                        .requestMatchers("/reporte/**", "/reportes/**", "/generar-reporte/**")
                        .hasAnyRole("ADMIN", "USER")

                        .anyRequest().authenticated())

                // Configuración del login
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll())
                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login") // redirige al landing después de cerrar sesión
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
