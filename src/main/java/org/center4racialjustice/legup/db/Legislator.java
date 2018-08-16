package org.center4racialjustice.legup.db;

import lombok.Data;
import org.center4racialjustice.legup.illinois.Name;

@Data
public class Legislator implements Identifiable {
    private Long id;
    private Long district;
    private String party;
    private String assembly;
    private Long year;
    private Person person;

    public static Legislator fromDomainLegislator(org.center4racialjustice.legup.domain.Legislator leg){
        Legislator legislator = new Legislator();

        legislator.setAssembly(leg.getAssembly().toString());
        legislator.setParty(leg.getParty());
        legislator.setDistrict((long) leg.getDistrict());
        legislator.setYear((long) leg.getSession());

        Name name = leg.getName();

        Person person = new Person();
        person.setLastName(name.getLastName());
        person.setFirstName(name.getFirstName());
        person.setMiddleName(name.getMiddleInitial());
        person.setSuffix(name.getSuffix());

        legislator.setPerson(person);

        return legislator;
    }
}
