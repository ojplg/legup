package org.center4racialjustice.legup.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.regex.Pattern;

public class TestNameParser {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    private static Map<String, Name> loadOverrides(){
        NameOverrides nameOverrides =  NameOverrides.load("conf/name.overrides");
        return nameOverrides.getOverrides();
    }

    @Test
    public void justLastName(){
        Map<String, Name> overrides = loadOverrides();
        NameParser parser = new NameParser(overrides);
        Name name = parser.fromLastNameFirstString("Jones");
        Assert.assertEquals("Jones", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void lastNameWithFirstInitial() {
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromLastNameFirstString("Brown, E.");
        Assert.assertEquals("Brown", name.getLastName());
        Assert.assertEquals("E", name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void lastNameOnlyWithApostrophe(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromLastNameFirstString("D'Amico");
        Assert.assertEquals("D'Amico", name.getLastName());
    }

    @Test
    public void fullName() {
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromLastNameFirstString("Barickman, Jason A");
        Assert.assertEquals("Barickman", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("Jason", name.getFirstName());
        Assert.assertEquals("A", name.getMiddleInitial());
    }

    @Test
    public void fullNameRegularOrder() {
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromRegularOrderString("Steven A. Andersson");
        Assert.assertEquals("Steven", name.getFirstName());
        Assert.assertEquals("A", name.getMiddleInitial());
        Assert.assertEquals("Andersson", name.getLastName());
    }

    @Test
    public void fullNameRegularOrderWithSuffix(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromRegularOrderString("Jaime M. Andrade, Jr.");
        Assert.assertEquals("Jaime", name.getFirstName());
        Assert.assertEquals("M", name.getMiddleInitial());
        Assert.assertEquals("Andrade", name.getLastName());
        Assert.assertEquals("Jr", name.getSuffix());
    }

    @Test
    public void fullNameRegularOrderWithMiddleAndSuffix(){
//        "Maurice A. West II"



        NameParser parser = new NameParser();
        Name curtis = parser.fromRegularOrderString("Curtis J. Tarver II");
        Assert.assertEquals("Curtis", curtis.getFirstName());
        Assert.assertEquals("J", curtis.getMiddleInitial());
        Assert.assertEquals("Tarver", curtis.getLastName());
        Assert.assertEquals("II", curtis.getSuffix());

    }

    @Test
    public void fullNameRegularOrderWithFunnySuffix(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromRegularOrderString("Jerry Costello, II");
        Assert.assertEquals("Jerry", name.getFirstName());
        Assert.assertEquals("Costello", name.getLastName());
        Assert.assertEquals("II", name.getSuffix());
    }

    @Test
    public void regularNameWithHyphen(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromRegularOrderString("Melissa Conyears-Ervin");
        Assert.assertEquals("Melissa", name.getFirstName());
        Assert.assertEquals("Conyears-Ervin", name.getLastName());
    }

    @Test
    public void nameWithApostrophe(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromRegularOrderString("John C. D'Amico");
        Assert.assertEquals("John", name.getFirstName());
        Assert.assertEquals("C", name.getMiddleInitial());
        Assert.assertEquals("D'Amico", name.getLastName());

    }

    @Test
    public void commaWithoutSpace(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromLastNameFirstString("Harris,David");
        Assert.assertEquals("David", name.getFirstName());
        Assert.assertEquals("Harris", name.getLastName());
    }

    @Test
    public void firstAndLastName(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromLastNameFirstString("Nybo, Chris");
        Assert.assertEquals("Nybo", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("Chris", name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());
    }

    @Test
    public void withSuffix(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromLastNameFirstString( "Clayborne Jr., James F");
        Assert.assertEquals("Clayborne", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertEquals("James", name.getFirstName());
        Assert.assertEquals("F", name.getMiddleInitial());
        Assert.assertEquals("Jr", name.getSuffix());
    }

    @Test
    public void lastNameMatchesRegex(){
        Assert.assertTrue(Pattern.matches(NameParser.unifiedRegex, "Smith"));
    }

    @Test
    public void fullNameMatchesRegex(){
        Assert.assertTrue(Pattern.matches(NameParser.unifiedRegex, "Jones, Samantha B"));
        Assert.assertTrue(Pattern.matches(NameParser.unifiedRegex, "Barickman, Jason A"));
    }

    @Test
    public void fullNameWithSuffix(){
        Assert.assertTrue(Pattern.matches(NameParser.unifiedRegex, "Clayborne Jr., James F"));
    }

    @Test
    public void twoPartLastName(){
        NameParser parser = new NameParser(loadOverrides());
        Name name = parser.fromLastNameFirstString("Van Pelt");
        Assert.assertEquals("Van Pelt", name.getLastName());
        Assert.assertNull(name.getFirstInitial());
        Assert.assertNull(name.getFirstName());
        Assert.assertNull(name.getMiddleInitial());

    }
}
