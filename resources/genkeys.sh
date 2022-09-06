rm -r cs_keys
rm -r ps_keys

mkdir cs_keys
openssl ecparam -name prime256v1 -genkey -noout -out cs_keys/cs_key.pem
openssl ec -in cs_keys/cs_key.pem -pubout -out cs_keys/cs_public.pem
openssl req -new -x509 -key cs_keys/cs_key.pem \
-passin pass:password --out cs_keys/cs_cert.pem -days 360 \
-subj "/C=IT/ST=.../L=.../O=.../OU=.../CN=gov/emailAddress=..."
mkdir ../crypto/src/main/resources
cp cs_keys/cs_cert.pem ../crypto/src/main/resources
cp ./* ../CentralStation/src/main/resources
mkdir ps_keys

for ID in 1 2 3
do
  openssl ecparam -name prime256v1 -genkey -noout -out ps_keys/ps${ID}_key.pem
  openssl ec -in ps_keys/ps${ID}_key.pem -pubout -out ps_keys/ps${ID}_public.pem
  mkdir ps${ID}_resources
  cp ps_keys/ps${ID}_key.pem ps${ID}_resources/ps_key.pem
  cp ps_keys/ps${ID}_key.pem cs_keys
  cp cs_keys/cs_public.pem ps${ID}_resources
done

mkdir v_keys
for ID in 2 3 4
do
  openssl ecparam -name prime256v1 -genkey -noout -out v_keys/v${ID}_key.pem
  openssl ec -in v_keys/v${ID}_key.pem -pubout -out v_keys/v${ID}_public.pem
  cp v_keys/v${ID}_public.pem ps1_resources
done
