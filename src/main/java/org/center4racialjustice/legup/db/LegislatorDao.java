package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.ChamberConverter;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LegislatorDao implements Dao<Legislator> {

    public static String table = "legislators";

    public static Supplier<Legislator> supplier = () -> new Legislator();

    public static List<TypedColumn<Legislator>> typedColumnList =
            Arrays.asList(
                    new LongColumn<>("ID", "", Legislator::getId, Legislator::setId),
                    new StringColumn<>("FIRST_NAME", "", Legislator::getFirstName, Legislator::setFirstName),
                    new StringColumn<>("MIDDLE_NAME_OR_INITIAL", "", Legislator::getMiddleInitialOrName, Legislator::setMiddleInitialOrName),
                    new StringColumn<>("LAST_NAME", "", Legislator::getLastName, Legislator::setLastName),
                    new StringColumn<>("SUFFIX", "", Legislator::getSuffix, Legislator::setSuffix),
                    new CodedEnumColumn<>("CHAMBER", "", Legislator::getChamber, Legislator::setChamber, ChamberConverter.INSTANCE),
                    new LongColumn<>("DISTRICT", "", Legislator::getDistrict, Legislator::setDistrict),
                    new StringColumn<>("PARTY", "", Legislator::getParty, Legislator::setParty),
                    new LongColumn<>("SESSION_NUMBER", "", Legislator::getSessionNumber, Legislator::setSessionNumber)
            );

    private final Connection connection;

    public LegislatorDao(Connection connection){
        this.connection = connection;
    }

    public long save(Legislator legislator){
        return DaoHelper.save(connection, table, typedColumnList, legislator);
    }

    public Legislator read(long id){
        List<Legislator> legislators =
                DaoHelper.read(connection, table, typedColumnList, Collections.singletonList(id), supplier);
        return DaoHelper.fromSingletonList(legislators, "Table " + table + ", ID " + id);
    }

    public List<Legislator> readAll(){
        List<Legislator> legislators =
                DaoHelper.read(connection, table, typedColumnList, Collections.emptyList(), supplier);
        return legislators;
    }

    public List<Legislator> readBySession(long session){
        StringBuilder sqlBldr = new StringBuilder();
        sqlBldr.append(DaoHelper.selectString(table, typedColumnList));
        sqlBldr.append(" where session_number = ");
        sqlBldr.append(session);
        String sql = sqlBldr.toString();

        return DaoHelper.read(connection, sql, typedColumnList, supplier);
    }
}
