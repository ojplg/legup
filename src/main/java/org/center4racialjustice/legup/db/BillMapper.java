package org.center4racialjustice.legup.db;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.center4racialjustice.legup.domain.Bill;

public interface BillMapper {
    @Select("SELECT * FROM bills WHERE id = #{id}")
    @Results(id = "billResult", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "number", column = "bill_number"),
            @Result(property = "session", column = "session_number"),
            @Result(property = "shortDescription", column = "short_description"),
            @Result(property = "chamber", column = "chamber", typeHandler = ChamberTypeHandler.class ),
    })
    Bill selectBill(int id);
}
