# mTLS Spring Boot Server

This is a Spring Boot 3 application demonstrating Mutual TLS (mTLS) with a custom X.509 certificate fingerprint validation filter.

## Features
- **mTLS Enabled**: Requiring client certificates (`client-auth=need`).
- **Fingerprint Validation**: A custom `OncePerRequestFilter` validates the SHA-256 fingerprint of the client certificate against a configured value.
- **REST Endpoint**: Secure endpoint at `/secure`.

## Prerequisites
- Java 17
- Maven 3.x

## How to Run

### 1. (Optional) Generate Certificates
The project comes with pre-generated self-signed certificates. If you wish to regenerate them:
```bash
cd scripts
./generate_certs.sh
```
> [!IMPORTANT]
> If you regenerate certificates, update `app.security.expected-client-fingerprint` in `src/main/resources/application.yml` with the new fingerprint printed by the script.

### 2. Start the Server
Run the following command from the `mtls-server` directory:
```bash
mvn spring-boot:run
```
The server will start on `https://localhost:8443`.

## How to Add Multiple Clients

The server is configured to allow a list of specific client certificate fingerprints.

### 1. Generate a New Client Certificate
Follow the certificate generation steps to create a new keypair and extract its SHA-256 fingerprint.

### 2. Update Server Configuration
Add the new fingerprint to the `expected-client-fingerprints` list in `src/main/resources/application.yml`:

```yaml
app:
  security:
    expected-client-fingerprints:
      - "EXISTING_FINGERPRINT_1"
      - "NEW_FINGERPRINT_2"
```

### 3. Trust the New Certificate
Ensure the new client's certificate (`.cer` file) is imported into the server's `truststore.p12`:
```bash
keytool -importcert -alias client2 -file client2.cer -keystore truststore.p12 -storepass password
```

## Configuration
Security settings are located in `src/main/resources/application.yml`.
- `server.ssl.*`: standard Spring Boot SSL properties.
- `app.security.expected-client-fingerprints`: A list of allowed SHA-256 fingerprints.
