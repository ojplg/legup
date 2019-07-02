package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
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

    public void addReportCard(ReportCard reportCard){
        if( reportCards == null ){
            reportCards = new ArrayList<>();
        }
        reportCards.add(reportCard);
    }

    public static boolean anyOwnCard(List<Organization> organizations, long reportCardId){
        return findReportCardOwner(organizations, reportCardId) != null;
    }

    public static Organization findReportCardOwner(List<Organization> organizations, long reportCardId){
        return Lists.findfirst(organizations, org -> org.ownsCard(reportCardId));
    }
}
