package org.center4racialjustice.legup.db;

import org.apache.ibatis.session.SqlSession;
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
                    new LongColumn<>("SESSION_NUMBER", "", Legislator::getSessionNumber, Legislator::setSessionNumber),
                    new StringColumn<>("MEMBER_ID", "" , Legislator::getMemberId, Legislator::setMemberId)
            );

    private final SqlSession sqlSession;
    private LegislatorMapper legislatorMapper;

    public LegislatorDao(SqlSession sqlSession){
        this.sqlSession = sqlSession;
        this.legislatorMapper = sqlSession.getMapper(LegislatorMapper.class);
    }

    public long save(Legislator legislator){
        if( legislator.getId() == null ) {
            legislatorMapper.insert(legislator);
            sqlSession.commit();
            return legislator.getId();
        } else {
            legislatorMapper.update(legislator);
            sqlSession.commit();
            return legislator.getId();
        }
    }

    public Legislator read(long id){
        return legislatorMapper.selectLegislator(id);
    }

    public List<Legislator> readAll(){
        return legislatorMapper.selectLegislators();
    }

    public List<Legislator> readBySession(long session){
        return legislatorMapper.selectLegislatorsBySession(session);
    }
}
