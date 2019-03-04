package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.List;

@Data
public class Organization {
    private Long id;
    private String name;
    private List<ReportCard> reportCards;

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean ownsCard(long reportCardId){
        for( ReportCard card : reportCards ){
            if ( card.getId().equals(reportCardId) ) {
                return true;
            }
        }
        return false;
    }
}
