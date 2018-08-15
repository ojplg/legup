package org.center4racialjustice.legup.illinois;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class TestName {

    @Test
    public void justLastName(){
        Name name = Name.fromLastNameFirstString("Jones");
        Assert.assertEquals("Jones", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void lastNameWithFirstInitial() {
        Name name = Name.fromLastNameFirstString("Brown, E.");
        Assert.assertEquals("Brown", name.getLastName());
        Assert.assertEquals("E", name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void fullName() {
        Name name = Name.fromLastNameFirstString("Barickman, Jason A");
        Assert.assertEquals("Barickman", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("Jason", name.getFirstName());
        Assert.assertEquals("A", name.getMiddleInitial());
    }

    @Test
    public void fullNameRegularOrder() {
        Name name = Name.fromRegularOrderString("Steven A. Andersson");
        Assert.assertEquals("Steven", name.getFirstName());
        Assert.assertEquals("A", name.getMiddleInitial());
        Assert.assertEquals("Andersson", name.getLastName());
    }

    @Test
    public void fullNameRegularOrderWithSuffix(){
        Name name = Name.fromRegularOrderString("Jaime M. Andrade, Jr.");
        Assert.assertEquals("Jaime", name.getFirstName());
        Assert.assertEquals("M", name.getMiddleInitial());
        Assert.assertEquals("Andrade", name.getLastName());
        Assert.assertEquals("Jr", name.getSuffix());
    }

    @Test
    public void fullNameRegularOrderWithFunnySuffix(){
        Name name = Name.fromRegularOrderString("Jerry Costello, II");
        Assert.assertEquals("Jerry", name.getFirstName());
        Assert.assertEquals("Costello", name.getLastName());
        Assert.assertEquals("II", name.getSuffix());
    }

    @Test
    public void takeMiddleName(){
        Name name = Name.fromRegularOrderString("Linda Chapa LaVia");
        Assert.assertEquals("Linda", name.getFirstName());
        Assert.assertEquals("Chapa", name.getMiddleInitial());
        Assert.assertEquals("LaVia", name.getLastName());
    }

    @Test
    public void regularNameWithHyphen(){
        Name name = Name.fromRegularOrderString("Melissa Conyears-Ervin");
        Assert.assertEquals("Melissa", name.getFirstName());
        Assert.assertEquals("Conyears-Ervin", name.getLastName());
    }

    @Test
    public void nameWithApostrophe(){
        Name name = Name.fromRegularOrderString("John C. D'Amico");
        Assert.assertEquals("John", name.getFirstName());
        Assert.assertEquals("C", name.getMiddleInitial());
        Assert.assertEquals("D'Amico", name.getLastName());

    }



    @Test
    public void firstAndLastName(){
        Name name = Name.fromLastNameFirstString("Nybo, Chris");
        Assert.assertEquals("Nybo", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("Chris", name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void withSuffix(){
        Name name = Name.fromLastNameFirstString( "Clayborne Jr., James F");
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
        Assert.assertTrue(Pattern.matches(Name.unifiedRegex, "Barickman, Jason A"));
    }

    @Test
    public void fullNameWithSuffix(){
        Assert.assertTrue(Pattern.matches(Name.unifiedRegex, "Clayborne Jr., James F"));
    }

    @Test
    public void twoPartLastName(){
        Name name = Name.fromLastNameFirstString("Van Pelt");
        Assert.assertEquals("Van Pelt", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());

    }



}
