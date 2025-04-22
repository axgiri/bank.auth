package github.axgiri.bankauthentication.configiration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PemConfig {
    public static RSAPrivateKey readPrivateKeyFromString(String pem, String algorithm) throws Exception {
        String privatePem = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s+", "");

        byte[] der = Base64.getDecoder().decode(privatePem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        PrivateKey key = kf.generatePrivate(spec);
        return (RSAPrivateKey) key;
    }
    
    public static RSAPublicKey readPublicKeyFromString(String pem, String algorithm) throws Exception {
        String publicPem = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s+", "");

        byte[] der = Base64.getDecoder().decode(publicPem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        PublicKey key = kf.generatePublic(spec);
        return (RSAPublicKey) key;
    }
}
