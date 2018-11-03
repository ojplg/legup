package org.center4racialjustice.legup.domain;

import lombok.Data;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String salt;
    private Organization organization;

    private static String encrypt(String password, String salt) {
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec ks = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 128);
            SecretKey s = f.generateSecret(ks);
            return new String(s.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex){
            throw new RuntimeException(ex);
        }
    }

    public static User createNewUser(String email, String unencryptedPassword){
        User user = new User();
        user.salt = newSalt();
        user.email = email;
        user.password = encrypt(unencryptedPassword, user.salt);
        return user;
    }

    public boolean correctPassword(String unencryptedPassword){
        String encryptedPassword = encrypt(unencryptedPassword, this.salt);
        return encryptedPassword.equals(this.password);
    }

    public static String newSalt(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return new String(bytes);
    }
}
