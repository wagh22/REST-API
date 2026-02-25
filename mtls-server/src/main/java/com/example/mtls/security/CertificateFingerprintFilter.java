package com.example.mtls.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HexFormat;

import java.util.List;

@Component
public class CertificateFingerprintFilter extends OncePerRequestFilter {

    @Value("${app.security.expected-client-fingerprints}")
    private List<String> expectedFingerprints;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0) {
            X509Certificate clientCert = certs[0];
            try {
                String actualFingerprint = calculateFingerprint(clientCert);
                boolean isAllowed = expectedFingerprints.stream()
                        .anyMatch(expected -> expected.equalsIgnoreCase(actualFingerprint));

                if (!isAllowed) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid certificate fingerprint");
                    return;
                }
            } catch (CertificateEncodingException | NoSuchAlgorithmException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error validating certificate");
                return;
            }
        } else {
            // Since client-auth=need is set, Spring Security/Tomcat should already handle
            // this,
            // but this is an extra safety check.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Client certificate missing");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String calculateFingerprint(X509Certificate cert)
            throws CertificateEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] der = cert.getEncoded();
        byte[] digest = md.digest(der);
        return HexFormat.of().withDelimiter(":").formatHex(digest).toUpperCase();
    }
}
