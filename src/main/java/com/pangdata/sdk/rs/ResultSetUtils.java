package com.pangdata.sdk.rs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ResultSetUtils.class);
	
	public static Object rsToObject(ResultSet rs, int type, String column) {
		try {
			if (type == Types.INTEGER) {
				return rs.getInt(column);
			} else if (type == Types.BIGINT) {
				return rs.getLong(column);
			} else if (type == Types.NUMERIC) {
				return rs.getInt(column);
			} else if (type == Types.REAL) {
				return rs.getInt(column);
			} else if (type == Types.SMALLINT) {
				return rs.getInt(column);
			} else if (type == Types.FLOAT) {
				return rs.getFloat(column);
			} else if (type == Types.DECIMAL
					|| type == Types.DOUBLE) {
				return rs.getDouble(column);
			} else {
				String value = rs.getString(column);
				if(value != null) {
					value = value.trim();
				}
				return value;
			}
		} catch (Exception e) {
			logger.error("ResultSet error", e);
			return null;
		}
	}
	
	public static Map<String, Integer> getMetaData(ResultSetMetaData md) throws SQLException {
		int columnCount = md.getColumnCount();
		Map<String, Integer> columns = new HashMap<String, Integer>();
		for (int i = 1; i <= columnCount; i++) {
			String name = md.getColumnName(i);
			int type = md.getColumnType(i);
			columns.put(name, type);

		}
		return columns;
	}
	
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("Connection error", e);				
			}
		}
	}
}
