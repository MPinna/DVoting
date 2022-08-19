rm -r cs_keys
rm -r ps_keys

mkdir cs_keys
openssl ecparam -name prime256v1 -genkey -noout -out cs_keys/cs_key.pem
openssl ec -in cs_keys/cs_key.pem -pubout -out cs_keys/cs_public.pem
openssl req -new -x509 -key cs_keys/cs_key.pem \
-passin pass:password --out cs_keys/cs_cert.pem -days 360 \
-subj "/C=IT/ST=.../L=.../O=.../OU=.../CN=gov/emailAddress=..."

mkdir ps_keys
openssl ecparam -name prime256v1 -genkey -noout -out ps_keys/ps1_key.pem
openssl ec -in ps_keys/ps1_key.pem -pubout -out ps_keys/ps1_public.pem

openssl req -new -key ps_keys/ps1_key.pem \
-out ps_keys/ps1_key.pem \
-subj "/C=IT/ST=.../L=.../O=.../OU=.../CN=ps1/emailAddress=..."
openssl x509 -req -in ps_keys/ps1_key.pem \
-CA cs_keys/cs_cert.pem  -CAkey cs_keys/cs_key.pem \
-CAcreateserial -out ps_keys/ps1_cert.pem -days 360 -sha256

