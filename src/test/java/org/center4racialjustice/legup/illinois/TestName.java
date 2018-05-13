package org.center4racialjustice.legup.illinois;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class TestName {

    @Test
    public void justLastName(){
        Name name = Name.fromAnyString("Jones");
        Assert.assertEquals("Jones", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void lastNameWithFirstInitial() {
        Name name = Name.fromAnyString("Brown, E.");
        Assert.assertEquals("Brown", name.getLastName());
        Assert.assertEquals("E", name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void fullName() {
        Name name = Name.fromAnyString("Barickman, Jason A");
        Assert.assertEquals("Barickman", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("Jason", name.getFirstName());
        Assert.assertEquals("A", name.getMiddleInitial());
    }

    @Test
    public void firstAndLastName(){
        Name name = Name.fromAnyString("Nybo, Chris");
        Assert.assertEquals("Nybo", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("Chris", name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void withSuffix(){
        Name name = Name.fromAnyString( "Clayborne Jr., James F");
        Assert.assertEquals("Clayborne", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("James", name.getFirstName());
        Assert.assertEquals("F", name.getMiddleInitial());
        Assert.assertEquals("Jr", name.getSuffix());
    }

    @Test
    public void lastNameMatchesRegex(){
        Assert.assertTrue(Pattern.matches(Name.unifiedRegex, "Smith"));
    }

    @Test
    public void fullNameMatchesRegex(){
        Assert.assertTrue(Pattern.matches(Name.unifiedRegex, "Jones, Samantha B"));
    }

}
