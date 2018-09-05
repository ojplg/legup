package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.DaoBuilder;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ChamberConverter;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.Arrays;
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
                    new LongColumn<>("SESSION_NUMBER", "", Legislator::getSessionNumber, Legislator::setSessionNumber),
                    new StringColumn<>("MEMBER_ID", "" , Legislator::getMemberId, Legislator::setMemberId)
            );

    private final Connection connection;
    private final org.center4racialjustice.legup.db.hrorm.Dao<Legislator> innerDao;

    public LegislatorDao(Connection connection){
        this.connection = connection;
        this.innerDao = DaoBuilders.LEGISLATORS.buildDao(connection);
    }

    public long save(Legislator legislator){
        if( legislator.getId() == null ){
            return innerDao.insert(legislator);
        } else {
            innerDao.update(legislator);
            return legislator.getId();
        }
    }

    public Legislator read(long id){
        return innerDao.select(id);
    }

    public List<Legislator> readAll(){
        return innerDao.selectAll();
    }

    public List<Legislator> readBySession(long session){
        Legislator legislator = new Legislator();
        legislator.setSessionNumber(session);
        return innerDao.selectManyByColumns(legislator, Arrays.asList("SESSION_NUMBER"));
    }
}
