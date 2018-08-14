package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class LegislatorDao {

    private static String table = "legislators";

    private static List<Column> columnList =
            Arrays.asList(
                    new Column<>("ID", ColumnType.Long, Legislator::getId, Legislator::setId),
                    new Column<>("DISTRICT", ColumnType.Long, Legislator::getDistrict, Legislator::setDistrict),
                    new Column<>("PARTY", ColumnType.String, Legislator::getParty, Legislator::setParty),
                    new Column<>("ASSEMBLY", ColumnType.String, Legislator::getAssembly, Legislator::setAssembly),
                    new Column<>("YEAR", ColumnType.Long, Legislator::getYear, Legislator::setYear),
                    new Column<>("PERSON_ID", ColumnType.Reference, Legislator::getPerson, Legislator::setPerson)
            );


    public LegislatorDao(Connection connection){

    }

    public long save(Legislator legislator){
        return -1;
    }

    public Legislator read(long id){
        return null;
    }
}
