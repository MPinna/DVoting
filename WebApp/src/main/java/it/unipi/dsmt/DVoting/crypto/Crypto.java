package it.unipi.dsmt.DVoting.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Random;

public  class Crypto {

    //  generate derivation and encoding vectors
    static byte[]  d = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
    static byte[]  e = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
    static byte[] iv =new byte[16];

    static IESParameterSpec param;
    public static void main(String[] args) {
        new Random().nextBytes(iv);
        param = new IESParameterSpec(null, null,256,256, iv);
        Security.addProvider(new BouncyCastleProvider());
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
        try {
            Cipher iesCipher = Cipher.getInstance(CYPHER);
            iesCipher.init(Cipher.ENCRYPT_MODE, public_key, param);
            return iesCipher.doFinal(plaintext);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(PrivateKey private_key, byte[] ciphertext) {
        try {
            Cipher iesCipher = Cipher.getInstance(CYPHER);
            iesCipher.init(Cipher.DECRYPT_MODE, private_key, param);
            return iesCipher.doFinal(ciphertext);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
