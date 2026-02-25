#!/bin/bash

# Configuration
PASSWORD="password"
VALIDITY=3650
DNAME="CN=localhost, OU=Example, O=Example, L=Example, ST=Example, C=US"
CLIENT_DNAME="CN=client-user, OU=Example, O=Example, L=Example, ST=Example, C=US"

# Clean up
rm -f *.p12 *.cer

echo "Generating Server KeyStore..."
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -storetype PKCS12 \
  -keystore keystore.p12 -validity $VALIDITY -storepass $PASSWORD -keypass $PASSWORD \
  -dname "$DNAME" -ext "SAN=dns:localhost,ip:127.0.0.1"

echo "Exporting Server Certificate..."
keytool -exportcert -alias server -keystore keystore.p12 -storepass $PASSWORD -file server.cer

echo "Generating Client KeyStore..."
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -storetype PKCS12 \
  -keystore client-keystore.p12 -validity $VALIDITY -storepass $PASSWORD -keypass $PASSWORD \
  -dname "$CLIENT_DNAME"

echo "Exporting Client Certificate..."
keytool -exportcert -alias client -keystore client-keystore.p12 -storepass $PASSWORD -file client.cer

echo "Generating Server TrustStore (containing client cert)..."
keytool -importcert -alias client -file client.cer -keystore truststore.p12 \
  -storepass $PASSWORD -noprompt

echo "Generating Client TrustStore (containing server cert)..."
keytool -importcert -alias server -file server.cer -keystore client-truststore.p12 \
  -storepass $PASSWORD -noprompt

echo "--------------------------------------------------"
echo "Client Certificate SHA-256 Fingerprint:"
keytool -list -v -keystore client-keystore.p12 -storepass $PASSWORD -alias client | grep "SHA256:" | cut -d ' ' -f 3
echo "--------------------------------------------------"

echo "Distributing certificates..."
# Server files
mkdir -p ../src/main/resources
cp keystore.p12 truststore.p12 ../src/main/resources/

# Client files
mkdir -p ../../mtls-client/resources
cp client-keystore.p12 client-truststore.p12 server.cer client.cer ../../mtls-client/resources/

# Clean up local copies
mv keystore.p12 truststore.p12 client-keystore.p12 client-truststore.p12 server.cer client.cer ./

echo "Done."
