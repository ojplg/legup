package org.center4racialjustice.legup.db;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;

import java.util.List;

public interface BillMapper {

    @Select("SELECT * FROM bills WHERE id = #{id}")
    @Results(id = "selectBill", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "number", column = "bill_number"),
            @Result(property = "session", column = "session_number"),
            @Result(property = "shortDescription", column = "short_description"),
            @Result(property = "chamber", column = "chamber", typeHandler = ChamberTypeHandler.class ),
    })
    Bill selectBill(long id);

    @Select("SELECT * FROM bills")
    @ResultMap("selectBill")
    List<Bill> selectBills();

    @Select("SELECT * FROM bills WHERE session_number = #{arg0} and chamber=#{arg1, typeHandler = org.center4racialjustice.legup.db.ChamberTypeHandler} and bill_number = #{arg2}")
    @ResultMap("selectBill")
    Bill selectBillBySessionChamberAndNumber(long session, Chamber chamber, long number);

    @Select("SELECT * FROM bills WHERE session_number = #{arg0}")
    @ResultMap("selectBill")
    List<Bill> selectBillsBySession(long session);

    @Insert("INSERT INTO BILLS (ID, BILL_NUMBER, SESSION_NUMBER, SHORT_DESCRIPTION, CHAMBER)  " +
            "VALUES (DEFAULT, #{number}, #{session}, #{shortDescription}, #{chamber, typeHandler = org.center4racialjustice.legup.db.ChamberTypeHandler}) ")
    @SelectKey(statement="select currval('bill_seq')", keyProperty="id", before=false, resultType=long.class)
    void insert(Bill bill);

    @Select("<script>SELECT * FROM bills WHERE id IN "
            + " <foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    @ResultMap("selectBill")
    List<Bill> selectByIds(List<Long> ids);
}
