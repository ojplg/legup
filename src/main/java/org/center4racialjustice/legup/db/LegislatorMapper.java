package org.center4racialjustice.legup.db;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.center4racialjustice.legup.domain.Legislator;

import java.util.List;

public interface LegislatorMapper {

    @Select("SELECT * FROM legislators WHERE id = #{id}")
    @Results(id = "selectLegislator", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "middleInitialOrName", column = "middle_name_or_initial"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "suffix", column = "suffix"),
            @Result(property = "chamber", column = "chamber", typeHandler = ChamberTypeHandler.class ),
            @Result(property = "district", column = "district"),
            @Result(property = "party", column = "party"),
            @Result(property = "sessionNumber", column = "session_number"),
            @Result(property = "memberId", column = "member_id"),
    })
    Legislator selectLegislator(long id);

    @Select("SELECT * FROM legislators")
    @ResultMap("selectLegislator")
    List<Legislator> selectLegislators();

    @Select("SELECT * FROM legislators WHERE session_number = #{arg0}")
    @ResultMap("selectLegislator")
    List<Legislator> selectLegislatorsBySession(long session);

    @Insert("INSERT INTO LEGISLATORS (ID, FIRST_NAME, MIDDLE_NAME_OR_INITIAL, LAST_NAME, SUFFIX, CHAMBER, DISTRICT, PARTY, SESSION_NUMBER, MEMBER_ID)  "
            + "VALUES (DEFAULT, #{firstName}, #{middleInitialOrName}, #{lastName}, #{suffix}, "
            + "#{chamber, typeHandler = org.center4racialjustice.legup.db.ChamberTypeHandler}, #{district}, #{party}, "
            + "#{sessionNumber}, #{memberId} ) ")
    @SelectKey(statement="select currval('legislator_seq')", keyProperty="id", before=false, resultType=long.class)
    void insert(Legislator legislator);

    @Update("UPDATE LEGISLATORS SET FIRST_NAME=#{firstName}, MIDDLE_NAME_OR_INITIAL= #{middleInitialOrName}, "
            + "LAST_NAME=#{lastName}, SUFFIX=#{suffix}, CHAMBER=#{chamber, typeHandler = org.center4racialjustice.legup.db.ChamberTypeHandler}, "
            + "DISTRICT=#{district}, PARTY=#{party}, SESSION_NUMBER=#{sessionNumber}, MEMBER_ID=#{memberId} "
            + "WHERE ID=#{id}")
    void update(Legislator legislator);

}
