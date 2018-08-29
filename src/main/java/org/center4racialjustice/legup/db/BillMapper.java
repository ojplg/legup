package org.center4racialjustice.legup.db;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.center4racialjustice.legup.domain.Bill;

import java.util.List;

public interface BillMapper {
    @Select("SELECT * FROM bills WHERE id = #{id}")
    @Results(id = "selectBillById", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "number", column = "bill_number"),
            @Result(property = "session", column = "session_number"),
            @Result(property = "shortDescription", column = "short_description"),
            @Result(property = "chamber", column = "chamber", typeHandler = ChamberTypeHandler.class ),
    })
    Bill selectBill(int id);

    @Select("SELECT * FROM bills")
    @Results(id = "selectAllBills", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "number", column = "bill_number"),
            @Result(property = "session", column = "session_number"),
            @Result(property = "shortDescription", column = "short_description"),
            @Result(property = "chamber", column = "chamber", typeHandler = ChamberTypeHandler.class ),
    })
    List<Bill> selectBills();


    @Insert("INSERT INTO BILLS (ID, BILL_NUMBER, SESSION_NUMBER, SHORT_DESCRIPTION, CHAMBER)  " +
            "VALUES (DEFAULT, #{number}, #{session}, #{shortDescription}, #{chamber, typeHandler = org.center4racialjustice.legup.db.ChamberTypeHandler})")
    void insert(Bill bill);
}
