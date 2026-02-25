# mTLS Java Client

This is a simple Java client demonstrating how to connect to an mTLS-secured Spring Boot server using Apache HttpClient 5.

## Features
- **mTLS Connection**: Loads client keystore and truststore to perform mutual authentication.
- **Apache HttpClient 5**: Uses the latest Apache HttpComponents for the connection.

## Prerequisites
- Java 17
- Maven 3.x

## How to Run

### 1. Ensure the Server is Running
Make sure the `mtls-server` is up and running on `https://localhost:8443`.

### 2. Run the Client
Run the following command from the `mtls-client` directory:
```bash
mvn exec:java -Dexec.mainClass="com.example.mtls.client.MTLSClient"
```

## Structure
- `src/main/java/com/example/mtls/client/MTLSClient.java`: The main client code.
- `resources/`: Contains the client keystore (`client-keystore.p12`) and the truststore (`client-truststore.p12`) containing the server's certificate.

## Supporting Multiple Clients
The server can be configured to support multiple independent clients. Each client should have its own unique keystore. To add a new client:
1. Generate a new client certificate.
2. Provide the fingerprint of the new certificate to the server administrator to add it to the `expected-client-fingerprints` list.
3. Ensure the new client certificate is added to the server's truststore.

## Troubleshooting
If you get an SSL handshake error, ensure that:
1. The server's certificate is in the client's `client-truststore.p12`.
2. The client's certificate is in the server's `truststore.p12`.
3. The passwords for the keystores/truststores match those in the code (`password`).
