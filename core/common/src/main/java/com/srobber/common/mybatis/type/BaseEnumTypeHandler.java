package com.srobber.common.mybatis.type;

import com.srobber.common.enums.BaseEnum;
import com.srobber.common.util.BaseEnumUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 枚举值类型处理器
 *
 * @author chensenlai
 */
public class BaseEnumTypeHandler<E extends BaseEnum> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public BaseEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getNum());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int num = rs.getInt(columnName);
        return num == 0 && rs.wasNull() ? null : BaseEnumUtil.of(type, num);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int num = rs.getInt(columnIndex);
        return num == 0 && rs.wasNull() ? null : BaseEnumUtil.of(type, num);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int num = cs.getInt(columnIndex);
        return num == 0 && cs.wasNull() ? null : BaseEnumUtil.of(type, num);
    }
}
