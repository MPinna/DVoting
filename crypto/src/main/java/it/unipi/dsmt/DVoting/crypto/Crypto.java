package it.unipi.dsmt.DVoting.crypto;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.Random;

public  class Crypto {


    public static void main(String[] args) {


        KeyPair kp=generateECKeys();
        byte[] cp= encrypt(kp.getPublic(), "eoeoeoooeoeo".getBytes());
        byte[] pl=decrypt(kp.getPrivate(), cp);
        System.out.println("plaintext: "+new String(pl));


    }
    //static final String EC_CURVE="brainpoolP384r1";
    static final String EC_CURVE="secp256r1";
    //static final String CYPHER="ECIES";
    static final String CYPHER="ECIESwithAES-CBC";


    public static KeyPair generateECKeys() {
        try {
            Security.addProvider(new BouncyCastleProvider());

            KeyPairGenerator ecKeyGen = KeyPairGenerator.getInstance("ECIES", BouncyCastleProvider.PROVIDER_NAME);
            ecKeyGen.initialize(new ECGenParameterSpec(EC_CURVE), new SecureRandom());

            return  ecKeyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(PublicKey public_key, byte[] plaintext) {
        byte[] iv =new byte[16];
        Security.addProvider(new BouncyCastleProvider());
        new Random().nextBytes(iv);
        IESParameterSpec param = new IESParameterSpec(null, null,256,256, iv);
        try {
            Cipher iesCipher = Cipher.getInstance(CYPHER);
            iesCipher.init(Cipher.ENCRYPT_MODE, public_key, param);
            byte[] cph = iesCipher.doFinal(plaintext);
            byte[] res = new byte[iv.length + cph.length];
            System.arraycopy(iv, 0, res, 0, iv.length);
            System.arraycopy(cph, 0, res, iv.length, cph.length);
            return res;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(PrivateKey private_key, byte[] ciphertext) {
        byte[] iv =new byte[16];
        byte[] cph =new byte[ciphertext.length-16];
        Security.addProvider(new BouncyCastleProvider());
        System.arraycopy(ciphertext, 0, iv, 0, iv.length);
        System.arraycopy(ciphertext, 16,cph , 0, cph.length);
        IESParameterSpec param = new IESParameterSpec(null, null,256,256, iv);
        try {
            Cipher iesCipher = Cipher.getInstance(CYPHER);
            iesCipher.init(Cipher.DECRYPT_MODE, private_key, param);
            return iesCipher.doFinal(cph);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey getPrivateKey(String filePath, String password) throws FileNotFoundException {
        File privateKeyFile = new File(filePath); // private key file in PEM format
        PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
        Object object = null;
        KeyPair kp = null;
        try {
            object = pemParser.readObject();
            PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (object instanceof PEMEncryptedKeyPair) {
                kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
                
            }
            assert kp != null;
            return kp.getPrivate();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static PrivateKey getPrivateKey(String filePath) throws FileNotFoundException {
        File privateKeyFile = new File(filePath); // private key file in PEM format
        PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
        Object object = null;
        KeyPair kp = null;
        try {
            object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            if (object instanceof PEMKeyPair) {
                kp = converter.getKeyPair((PEMKeyPair)object);

            }
            assert kp != null;
            return kp.getPrivate();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static PublicKey getPublicKey(String filePath) throws FileNotFoundException {
        Security.addProvider(new BouncyCastleProvider());
        File publicKeyFile = new File(filePath); // public key file in PEM format
        PEMParser pemParser = new PEMParser(new FileReader(publicKeyFile));
        Object object = null;
        KeyPair kp;
        try {
            object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (object instanceof PEMEncryptedKeyPair) {
                SubjectPublicKeyInfo spi =SubjectPublicKeyInfo.getInstance(pemParser.readObject());
                return converter.getPublicKey(spi);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;

    }

    public static PublicKey getPublicKeyFromCertificate(String certFilePath) throws FileNotFoundException  {

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(certFilePath);
            CertificateFactory f = new CertificateFactory();
            Certificate certificate = f.engineGenerateCertificate(fin);
            return  certificate.getPublicKey();

        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }

    }
}
