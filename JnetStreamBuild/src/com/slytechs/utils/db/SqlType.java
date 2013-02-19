/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.db;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps various JDBC data type primitives to our generic types.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum SqlType {
	Array(Types.ARRAY),
	BigInt(Types.BIGINT),
	Binary(Types.BINARY),
	Bit(Types.BIT),
	Blob(Types.BLOB),
	Boolean(Types.BOOLEAN),
	Char(Types.CHAR),
	CLOB(Types.CLOB),
	DataLink(Types.DATALINK),
	Date(Types.DATE),
	Decimal(Types.DECIMAL),
	Distinct(Types.DISTINCT),
	Double(Types.DOUBLE),
	Float(Types.FLOAT),
	Integer(Types.INTEGER),
	JavaObject(Types.JAVA_OBJECT),
	LongVarBinary(Types.LONGVARBINARY),
	LongVarChar(Types.LONGVARCHAR),
	Null(Types.NULL),
	Numeric(Types.NUMERIC),
	Other(Types.OTHER),
	Real(Types.REAL),
	Ref(Types.REF),
	SmallInt(Types.SMALLINT),
	Struct(Types.STRUCT),
	Time(Types.TIME),
	Timestamp(Types.TIMESTAMP),
	TinyInt(Types.TINYINT),
	VarBinary(Types.VARBINARY),
	VarChar(Types.VARCHAR)
	;
	
	private final int sqlType;

	private SqlType(int sqlType) {
		this.sqlType = sqlType;		
	}

	/**
	 * For this specified type return the equivelent SQL type.
	 * 
	 * @return
	 *   SQLType that matches our database the closest.
	 */
	public int getSqlType() {
		return sqlType;
	}
	
	/**
	 * Interface which allows conversion to and from SQL data types of
	 * whatever the underlying native object.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface SqlObject<T> {
		
		/**
		 * JDBC SQL data type for this object.
		 * 
		 * @return
		 *  JDBC SQL data type.
		 */
		public SqlType getSqlType();
		
		/**
		 * The actual SQL object hidden by this SqlObject interface.
		 * 
		 * @return
		 *   SQL object.
		 */
		public Object getSqlObject();
		
		/**
		 * Converts to a java native primitive.
		 * 
		 * @param returnType
		 *   Return type assumed for this object.
		 *   
		 * @param sqlData
		 *   SQL Data object to be converted into java native.
		 *   
		 * @return
		 *   Converted object.
		 */
		public T getNativeObject(Class<T> returnType, Object sqlData);
	}
	
	/**
	 * Factory method for creating the mapped SQL and our objects.
	 * 
	 * @param <T> Data type for this object.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface SqlObjectFactory<T> {

		/**
		 * Creates this SQL object.
		 * 
		 * @param data
		 *   Our data object to convert to SQL compatible type.
		 * @return
		 */
		public Object getSqlObject(T data);
		
		/**
		 * The SQL type that was mapped for this object type.
		 * 
		 * @return
		 *   JDBC SQL type.
		 */
		public SqlType getSqlType();
		
		/**
		 * Our data object again.
		 * 
		 * @param returnType 
		 *   Type of object we are expecting.
		 *   
		 * @param sqlData
		 *   SQL data object to convert to our type.
		 *   
		 * @return
		 *   Converted object in requested type.
		 */
		public T getNativeObject(Class<T> returnType, Object sqlData);
	}
	
	/**
	 * Mapper that maps our object to SQL objects.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public static class SqlObjectMapper {
		
		private static SqlObjectMapper defaultMapper;
		
		/**
		 * Factory method that gets the default instance of this mapper.
		 * 
		 * @return
		 *   Default instance of this mapper. One is created if doesn't exist already.
		 */
		public static SqlObjectMapper getDefault() {
			if (defaultMapper == null) {
				defaultMapper = new SqlObjectMapper();
			}
			return defaultMapper;
		}
		
		private Map<Class, SqlObjectFactory> map = new HashMap<Class, SqlObjectFactory>();
		
		/**
		 * Register our data types with the mapper for sql counter parts.
		 * 
		 * @param c
		 *   Our datatype to register.
		 *   
		 * @param factory
		 *   Factory object that knows how to create object of this type.
		 */
		public void registerFactory(Class c, SqlObjectFactory factory) {
			map.put(c, factory);
		}
		
		/**
		 * Converts our object type to SQL counterpar.
		 * 
		 * @param data
		 *   Our object to convert.
		 *   
		 * @return
		 *  Mapped SQL counter part.
		 */
		@SuppressWarnings("unchecked")
		public Object toSqlObject(Object data) {
			SqlObjectFactory factory = map.get(data.getClass());
			if (factory == null) {
				return data;
			}
			
			return factory.getSqlObject(data);
		}
		
		/**
		 * Converts from SQL object to our native object.
		 * 
		 * @param <T> 
		 *   The type we expect this object to be.
		 * 
		 * @param c
		 *   Class of the object.
		 *   
		 * @param sqlData
		 *   SQL object to convert.
		 *   
		 * @return
		 *   Our converted object.
		 */
		@SuppressWarnings("unchecked")
		public <T> T toNativeObject(Class<T> c, Object sqlData) {
			SqlObjectFactory factory = map.get(c);
			if (map == null && c.isInstance(sqlData)) {
				return (T)sqlData;
			}
			
			return (T) factory.getNativeObject(c, sqlData);
		}
	}

}
