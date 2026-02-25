package com.example.mtls.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CertificateFingerprintFilter fingerprintFilter;

    public SecurityConfig(CertificateFingerprintFilter fingerprintFilter) {
        this.fingerprintFilter = fingerprintFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .x509(x509 -> x509
                        .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                        .userDetailsService(username -> new org.springframework.security.core.userdetails.User(
                                username, "", java.util.Collections.emptyList())))
                .addFilterAfter(fingerprintFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}
