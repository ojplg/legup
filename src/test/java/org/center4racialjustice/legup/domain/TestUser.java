package org.center4racialjustice.legup.domain;

import org.junit.Assert;
import org.junit.Test;

public class TestUser {

    @Test
    public void testSaltGeneration(){
        String salt1 = User.newSalt();
        String salt2 = User.newSalt();

        Assert.assertNotNull(salt1);
        Assert.assertNotNull(salt2);

        Assert.assertNotEquals(salt1, salt2);

        Assert.assertTrue( salt1.length() > 20);
        Assert.assertTrue( salt2.length() > 20);
    }

    @Test
    public void testCreateNewUser(){

        String unencryptedPassword = "p@ssw0rd1!";

        User user = User.createNewUser("foo@bar.com", unencryptedPassword);

        Assert.assertNotNull(user.getSalt());
        Assert.assertTrue(user.getSalt().length() > 20);

        Assert.assertNotNull(user.getPassword());
        Assert.assertNotEquals(unencryptedPassword, user.getPassword());

        Assert.assertTrue(user.getPassword().length() > 10);
    }

    @Test
    public void testMatchesPassword(){

        String unencryptedPassword = "p@ssw0rd1!";

        User user = User.createNewUser("foo@bar.com", unencryptedPassword);

        Assert.assertTrue(user.correctPassword(unencryptedPassword));
    }

    @Test
    public void testMatchesPasswordNoticesWrongPasswords(){

        String unencryptedPassword = "p@ssw0rd1!";

        User user = User.createNewUser("foo@bar.com", unencryptedPassword);

        Assert.assertFalse(user.correctPassword("password"));
    }

    @Test
    public void testForZeroes(){

        String unencryptedPassword = "p@ssw0rd1!";

        for(int idx=0; idx<100; idx++) {
            User user = User.createNewUser("foo@bar.com", unencryptedPassword);

            Assert.assertFalse(User.checkForZeroes(user.getEmail(), "email" + idx));
            Assert.assertFalse(User.checkForZeroes(user.getSalt(), "salt" + idx));
            Assert.assertFalse(User.checkForZeroes(user.getPassword(), "password" + idx));
        }

    }

}
