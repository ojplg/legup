package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.web.responders.SaveResetPassword;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Data
public class User {

    private static final Logger log = LogManager.getLogger(SaveResetPassword.class);

    private Long id;
    private String email;
    private String password;
    private String salt;
//    private Organization organization;

    private static String encrypt(String password, String salt) {
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec ks = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 10000, 128);
            SecretKey s = f.generateSecret(ks);
            return new String(s.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex){
            throw new RuntimeException(ex);
        }
    }

    public static User createNewUser(String email, String unencryptedPassword){
        User user = new User();
        user.email = email;
        user.internalSetPassword(unencryptedPassword);
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


    public static boolean checkForZeroes(String s, String msg){
        for(byte b : s.getBytes()){
            if ( b == 0 ){
                log.info("FOUND A ZERO BYTE! " + msg);
                return true;
            }
        }
        return false;
    }

    public void resetPassword(String newUnencryptedPassword){
        internalSetPassword(newUnencryptedPassword);
    }

    public void internalSetPassword(String unencryptedPassword){
        String salt = newSalt();
        String password = encrypt(unencryptedPassword, salt);

        while ( checkForZeroes(salt, "salt") || checkForZeroes(password, "password") ){
            salt = newSalt();
            password = encrypt(unencryptedPassword, salt);
        }

        this.salt = salt;
        this.password = password;
    }

    public boolean isSuperUser(){
        // TODO: This should be a variable on the DB table
        return email.equals("lees@ripco.com");
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
