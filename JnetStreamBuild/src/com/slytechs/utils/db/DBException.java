/**
 * $Id$
 *
 * Copyright (C) 2003 - 2005  Mark Bednarczyk
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

import java.sql.SQLException;

import com.slytechs.utils.db.DatabaseUtils.DatabaseType;


/**
 * All our database exception subclass this DBException.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DBException extends Exception {
	
	private static final long serialVersionUID = -8651525896593532005L;
	private SQLException originalSQLException;
	protected DatabaseType dbType;
	protected String host;

	/**
	 * Empty exception with no message.
	 */
	public DBException() {
		super();
	}

	/**
	 * Exception initialized directly from database properties.
	 * 
	 * @param dbProperties 
	 *  DB properties to use to set various components of this exception.
	 *  
	 * @param msg
	 *  The message of this exception.
	 */
	public DBException(DBProperties dbProperties, String msg) {
		super(msg);
		
		this.dbType = dbProperties.getDbType();
		this.host = dbProperties.getDbHost();
	}

	/**
	 * Exception initialized directly from database properties.
	 * 
	 * @param dbProperties 
	 *  DB properties to use to set various components of this exception.
	 *  
	 * @param msg
	 *  The message of this exception.
	 *  
	 * @param cause
	 *   Original SQL cause exception.
	 */
	public DBException(DBProperties dbProperties, String msg, SQLException cause) {
		super(msg, cause);
		
		this.dbType = dbProperties.getDbType();
		this.host = dbProperties.getDbHost();
		this.originalSQLException = cause;
	}

	/**
	 * Returns the original SQL or cause exception.
	 * 
	 * @return
	 *   SQL cause.
	 */
	public SQLException getOriginalSQLException() {
		return originalSQLException;
	}

	/**
	 * Sets the cause exception
	 * 
	 * @param originalSQLException
	 *   The cause exception.
	 */
	public void setOriginalSQLException(SQLException originalSQLException) {
		this.originalSQLException = originalSQLException;
	}

	/**
	 * Datbase host this exception occured on.
	 * 
	 * @return
	 *   Hostname or IP address of the database host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the database host for this exception.
	 * 
	 * @param host
	 *   The database host.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Sets the DatabaseType that caused this exception.
	 * 
	 * @return
	 *  Database type.
	 */
	public DatabaseType getDbType() {
		return dbType;
	}

	/**
	 * Sets the database type that caused this exception.
	 * 
	 * @param dbType
	 *   Database type.
	 */
	public void setDbType(DatabaseType dbType) {
		this.dbType = dbType;
	}

}
