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

import java.sql.SQLException;


/**
 * Connection request to database failed because database is unreachable and not
 * ready. Report this type of error to the user and have him startup up the database.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DBDatabaseUnreachable extends DBSimpleException {
	
	private static final long serialVersionUID = 95368881787713899L;

	private static final String MSG = "Database is unreachable";
	
	private String database;

	/**
	 * Exception initialized directly from database properties.
	 * 
	 * @param dbProperties 
	 *  DB properties to use to set various components of this exception.
	 */
	public DBDatabaseUnreachable(DBProperties dbProperties) {
		super(dbProperties, MSG);
		
		this.database = dbProperties.getDbDatabaseName();

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
	public DBDatabaseUnreachable(DBProperties dbProperties, String msg, SQLException cause) {
		super(dbProperties, MSG, cause);
		
		this.database = dbProperties.getDbDatabaseName();
	}

	/**
	 * Database name.
	 * 
	 * @return
	 *  Database name.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Sets the database name for this exception.
	 * 
	 * @param database
	 *   Database name.
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * Formats this exception to a String.
	 * 
	 * @return
	 *  String representation of this message.
	 */
	public String toString() {
		String s = super.toString();
		
		return s + " (type=" + getDbType().toString() + ", host=" + getHost() + ", database=" + getDatabase() + ")";
	}

}
