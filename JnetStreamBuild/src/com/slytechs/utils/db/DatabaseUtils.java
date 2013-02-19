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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import com.slytechs.utils.namespace.NamedObject;
import com.slytechs.utils.number.BitFlag;



/**
 * A number of database specificic utility methods and constants. Many different
 * database types are enclosed and DatabaseType enum that contains most of the needed
 * constants to load the driver and 
 * 
 * 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DatabaseUtils {
	
	/**
	 * Defines the type of database this is. Maintains some specific
	 * instantiation information about each type of database such as:
	 * <UL>
	 *  <LI> JDBC driver class name
	 *  <LI> Vendor specific error code matching to more general one.
	 * </UL> 
	 *
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public  enum DatabaseType {
		/**
		 * MySQL specific constants
		 */
		MYSQL 		("com.mysql.jdbc.Driver") {	
			public boolean matchCode(DBCode code, String match) {	
				return code.mysql.equals(match);
			}
		},
		/**
		 * Oracle specific constants
		 */
		ORACLE_THIN ("oracle.jdbc.OracleDriver") {	
			public boolean matchCode(DBCode code, String match) {	
				return code.oracle.equals(match);
			}
		},
		
		/**
		 * Sybase specific constants
		 */
		SYBASE      ("com.sybase.jdbc3.jdbc.SybDriver") {	
			public boolean matchCode(DBCode code, String match) {
				return code.sybase.equals(match);
			}
		},
		
		/**
		 * PostGRE specific constants
		 */
		POSTGRE_SQL ("org.postgresql.Driver") {	
			public boolean matchCode(DBCode code, String match) {
				return code.postgre.equals(match);
			}
		},
		
		/**
		 * Informix specific constants
		 */
		IBM_INFORMIX("com.informix.jdbc.IfxDriver") {	
			public boolean matchCode(DBCode code, String match) {
				return code.informix.equals(match);
			}
		};
		
		protected abstract boolean matchCode(DBCode code, String match);
		
		/**
		 * Given a vendor code returned by the JDBC driver, map it to
		 * a specific DBCode defined here.
		 * 
		 * @param 
		 *   match Vendor error code to match.
		 *   
		 * @return
		 *   Our mapped DBCode.
		 */
		public DBCode matchCode(String match) {
			
			for(DBCode code: DBCode.values()) {
				if (matchCode(code, match)) {
					return code;
				}
			}
			
			return null;
		}
		
		/**
		 * Enum structure that defines lots of Vendor error codes.
		 * 
		 * @author Mark Bednarczyk
		 * @author Sly Technologies, Inc.
		 */
		public static enum DBCode {
			/**
			 * Database unrachable Error code.
			 */
			DATABASE_UNREACHABLE("08S01", "08001", "JZ003", "08004", "08001" ) {
				public DBException toDbException(DBProperties properties) {
					return new DBDatabaseUnreachable(properties);
				}	
			},
			
			/**
			 * Database table does not exist error code.
			 */
			TABLE_DOESNT_EXIST("42S02", "42S02", "42S02", "42S02", "42S02" ) {
				public DBException toDbException(DBProperties properties) {
					return new DBTableDoesntExist(properties);
				}
			};
			
			final String mysql;
			final String oracle;
			final String sybase;
			final String postgre;
			final String informix;
			
			/**
			 * Method which maps this error code, given various database properties
			 * to a DBException matching this error code.
			 * 
			 * @param 
			 *   properties database properties that describe this database.
			 *   
			 * @return
			 *   Mapped DBException specific for this error code.
			 */
			public abstract DBException toDbException(DBProperties properties);

			private DBCode(String mysql, String oracle, String sybase, String postgre, String informix) {
				this.mysql = mysql;
				this.oracle = oracle;
				this.sybase = sybase;
				this.postgre = postgre;
				this.informix = informix;
			}	
		}
						
		private final String driverName;
		
		private DatabaseType(final String driverName) {
			this.driverName = driverName;			
		}
		
		/**
		 * Causes the specific DatabaseType JDBC to be loaded and registered
		 * with JDBC driver manager.
		 * 
		 * @throws ClassNotFoundException
		 *   Driver was not found in the classpath.
		 *   
		 * @throws IllegalAccessException 
		 *   You do not have security access to load the driver. 
		 *   
		 * @throws InstantiationException 
		 *   Error occured while loading the driver.
		 */
		public void loadJDBCDriver() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
			
			Class.forName(driverName).newInstance();
		}
		
		/**
		 * Creates a URL and then a connection object to the database described by the properites.
		 * 
		 * @param dbProperties Properties which describe the database parameters such as user name
		 * user password and database host. The differences in URL creations and connection
		 * established are masked.
		 * 
		 * @return
		 *   Established connection to the database.
		 *   
		 * @throws SQLException
		 *   Any SQL errors during connection attempt.
		 */
		public Connection getConnection(DBProperties dbProperties) throws SQLException {
			String url = createURL(dbProperties);
			
			return getConnection(url);
		}
		
		/**
		 * Given a URL will create a connection to this specific database using some specific
		 * information.
		 * 
		 * @param url 
		 *   url to use for the connecition.
		 *   
		 * @return
		 *   Established connection object.
		 *   
		 * @throws SQLException
		 *   Any SQL errors during connection attempt.
		 */
		public Connection getConnection(String url) throws SQLException {
			return DriverManager.getConnection(url);
		}
	
		/**
		 * Gets name of the driver class for this DatabaseType.
		 * 
		 * @return 
		 *   name of the JDBC driver class.
		 */
		public String getJDBCDriverName() {
			return driverName;
		}
		
		/**
		 * Builds a specific URL for this DatabaseType.
		 * 
		 * @param dbProperties
		 *   Properties that describe the connection properties such ash
		 *   user name, user passwrd, database host.
		 *   
		 * @return
		 *   String representing the properly formatted URL to initiate a connection
		 *   to this DatabaseType.
		 */
		public String createURL(DBProperties dbProperties) {
			String host = dbProperties.getDbHost();
			String database = dbProperties.getDbDatabaseName();
			String user = dbProperties.getDbUser();
			String password = dbProperties.getDbUserPassword();

			return createURL(host, database, user, password);
		}
		
		/**
		 * Builds a specific URL for this DatabaseType.
		 * 
		 * @param host 
		 *   Database host.
		 *   
		 * @param database
		 *   Database name to connect to.
		 *   
		 * @param user
		 *  User name to use for the connection.
		 *  
		 * @param password
		 *  Database user password to use for the connection.
		 *  
		 * @return
		 *   String representation of this properly formatted URL for this DatabaseType.
		 */
		public String createURL(String host, String database, String user, String password) {
			StringBuilder buf = new StringBuilder("jdbc:");
			
			switch(this) {
			case MYSQL:
				buf.append("mysql://");
				break;
				
			case POSTGRE_SQL:
				buf.append("postgresql://");
				break;
				
			case SYBASE:
				buf.append("sybase:Tds:");
				break;
	
			case ORACLE_THIN:
				buf.append("oracle-thin:://");
				break;
				
			case IBM_INFORMIX:
				buf.append("informix:://");
				break;
			
			default:
					throw new IllegalStateException("Unexpected database type of " + toString());
			}
			
			buf.append(host).append("/").append(database);
			buf.append("?").append("user=").append(user);
			buf.append("&").append("password=").append(password);
			
			return buf.toString();
		}
		
		/**
		 * Maps a JDBC returned SQLException and the returned vendor error code to appropriately mapped
		 * DBException that is specific to the error. This static method will determine the correct
		 * DatabaseType to use for mapping of the vendor error code.
		 * 
		 * @param sqlException
		 *   Original SQLException as returned by the JDBC driver.
		 *   
		 * @param dbProperties
		 *   Properties that were used to connect to the database.
		 *   
		 * @return
		 *   Mapped specific DBException subclass specific to the vendor error code.
		 */
		public static DBException convertToDbException(SQLException sqlException, DBProperties dbProperties) {
			DatabaseType dbType = dbProperties.getDbType();
			
			return dbType.convert(sqlException, dbProperties);
		}
		
		/**
		 * Maps a JDBC returned SQLException and the returned vendor error code to appropriately mapped
		 * DBException that is specific to the error. 
		 * 
		 * @param sqlException
		 *   Original SQLException as returned by the JDBC driver.
		 *   
		 * @param dbProperties
		 *   Properties that were used to connect to the database.
		 *   
		 * @return
		 *   Mapped specific DBException subclass specific to the vendor error code.
		 */

		public DBException convert(SQLException sqlException, DBProperties dbProperties) {
			DBException dbe = null;;
			String state = sqlException.getSQLState();
			
			DBCode code = matchCode(state);
			
			if (code == null) {
				return new DBException(dbProperties, "Cought unrecognized SQL Exception for database type of " + toString(), sqlException);
			}

			dbe = code.toDbException(dbProperties);
			
			return dbe;
		}
	}

	/**
	 * Integer based Database specific flags that can be easily stored in a database as 
	 * an integer.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public static enum DatabaseFlag implements BitFlag {
		
		/**
		 * This database is designated to be the primary database.
		 */
		PRIMARY(0x01), 
		
		/**
		 * This database is designated to be the redundant/backup database for the 
		 * primary database.
		 */
		REDUNDANT(0x02),
		
		/**
		 * This database is a child, or dependant database on the primary database.
		 */
		CHILD(0x04),
		;

		private int bits = 0x00;

		private DatabaseFlag(final int flag) {
			this.bits = flag;
		}

		/**
		 * turns the integer bits of this entire flag.
		 * 
		 * @return
		 *   Integer flag representation.
		 */
		public int bits() {
			return bits;
		}

		/**
		 * Sets the supplied bits in this flag.
		 * 
		 * @param bits
		 *   The bits to set in this flag.
		 */
		public int set(int bits) {
			return bits | this.bits;
		}

		/**
		 * Clears the specified bits in this flag.
		 * 
		 * @param bits
		 *   The bits to clear in this flag.
		 */
		public int clear(int bits) {
			return bits & ~this.bits;
		}

		/**
		 * Checks if the supplied bits are set in this flag.
		 * 
		 * @param bits
		 *  The bits to check if they are set.
		 */
		public boolean isSet(int bits) {
			return (bits & this.bits) != 0;
		}

		/**
		 * Converts the binary integer to string labels representing
		 * the bit state of this flag.
		 * 
		 * @param flags
		 *   Flags to convert to string labels.
		 *   
		 * @return
		 *   String label representing the bits within this flag. Suitable
		 *   for printout and debug.
		 */
		public static String flagsToString(int flags) {
			return flagsToString(new StringBuilder(), flags).toString();
		}

		/**
		 * Converts the supplied bits to string label and stores the result
		 * in a string builder buffer.
		 * 
		 * @param buf 
		 *   Buffer to store the string labels.
		 *   
		 * @param flags
		 *   Flags to map to labels.
		 *   
		 * @return
		 *   The string builder buffer that holds the labels. Is the same as the
		 *   buf parameter supplied.
		 */
		public static StringBuilder flagsToString(StringBuilder buf, int flags) {

			String separator = "";

			for (DatabaseFlag flag : values()) {

				if (flag.isSet(flags)) {
					buf.append(separator);
					buf.append(flag.toString());
					separator = ", ";
				}
			}

			return buf;
		}

		/**
		 * Converts the set of flags in the integer to an array of labels
		 * for each flag.
		 * 
		 * @param flags
		 *   Flag to get the labels for.
		 *   
		 * @return
		 *   Array of labels for each bit in the supplied flags argument.
		 */
		public static String[] flagsToStringArray(int flags) {
			/*
			 * First count bits set 
			 */

			int count = 0;
			for (DatabaseFlag flag : values()) {
				if (flag.isSet(flags)) {
					count++;
				}
			}
			String[] sa = new String[count];
			for (DatabaseFlag flag : values()) {
				if (flag.isSet(flags)) {
					sa[--count] = flag.toString();
				}
			}

			return sa;
		}
	}
	
	/**
	 * Interface implemented by database adapters. They contain string
	 * definition for a prepared statement.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface DBStatement {
		
		public static final String PARAMETER = "?";
		
		public String getStatementString(DatabaseType dbType, String tableName);
		
		public int getParameterCount(DatabaseType dbType);
		
		public void fillStatement(PreparedStatement pst, NamedObject table, Iterator values) throws SQLException;
	}
	
	/**
	 * Interface which allows retrieval of Connection and DatabaseType information after
	 * the database connection has been established.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface DBConnector {
		
		/**
		 * The a connection to the database.
		 * 
		 * @return
		 *   JDBC connection to the database object.
		 *   
		 * @throws SQLException
		 *   Any SQL errors that may have occured during the connection attempt.
		 */
		public Connection getConnection() throws SQLException;

		/**
		 * Returns the DatabaseType this database object is representing. This DatabaseType
		 * will be Vendor specific as defined in the DatabaseType enum structure.
		 * 
		 * @return
		 *   The type of database this is.
		 */
		public DatabaseType getDbType();
	}

}
