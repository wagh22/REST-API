package com.example.mtls.client;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

public class MTLSClient {

    public static void main(String[] args) throws Exception {
        String keyStorePath = "resources/client-keystore.p12";
        String trustStorePath = "resources/client-truststore.p12";
        char[] password = "password".toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream is = Files.newInputStream(Paths.get(keyStorePath))) {
            keyStore.load(is, password);
        }

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (InputStream is = Files.newInputStream(Paths.get(trustStorePath))) {
            trustStore.load(is, password);
        }

        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, password)
                .loadTrustMaterial(trustStore, null)
                .build();

        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(sslContext)
                        .build())
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build()) {

            HttpGet request = new HttpGet("https://localhost:8443/secure");
            System.out.println("Executing request " + request.getMethod() + " " + request.getUri());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
        }
    }
}
