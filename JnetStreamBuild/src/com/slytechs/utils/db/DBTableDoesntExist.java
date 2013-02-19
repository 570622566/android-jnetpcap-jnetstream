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
 * Exception when table in a query doesn't exist.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DBTableDoesntExist extends DBException {
	
	private static final long serialVersionUID = -782980207282465003L;
	
	/**
	 * Default message.
	 */
	public static final String MSG = "Table doesn't exist";

	/**
	 * Exception initialized directly from database properties.
	 * 
	 * @param dbProperties 
	 *  DB properties to use to set various components of this exception.
	 *  
	 * @param msg
	 *  The message of this exception.
	 */
	public DBTableDoesntExist(DBProperties dbProperties, String msg) {
		super(dbProperties, msg);
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
	 *  @param cause
	 *   Original SQL exception.
	 */
	public DBTableDoesntExist(DBProperties dbProperties, String msg, SQLException cause) {
		super(dbProperties, msg, cause);
	}

	/**
	 * Exception initialized directly from database properties.
	 * 
	 * @param dbProperties 
	 *  DB properties to use to set various components of this exception.
	 */
	public DBTableDoesntExist(DBProperties properties) {
		super(properties, MSG);
	}


}
