package common.util.tools;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * SQL工具集
 * 
 * @author jieli
 *
 */
public class SqlUtils {
	public static <T> int convert2SqlType(T obj) throws SQLException {
		if (obj instanceof String)
			return Types.VARCHAR;
		if (obj instanceof Long)
			return Types.BIGINT;
		if (obj instanceof Integer)
			return Types.INTEGER;
		if (obj instanceof Short)
			return Types.TINYINT;
		if (obj instanceof Timestamp)
			return Types.TIMESTAMP;
		if (obj instanceof BigDecimal)
			return Types.DECIMAL;
		if (obj instanceof Boolean)
			return Types.BIT;
		throw new SQLException("SqlType NOT support");
	}
}
