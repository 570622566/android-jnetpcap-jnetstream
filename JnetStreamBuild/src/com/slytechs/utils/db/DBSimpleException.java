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
 * Simple exception is an exception that might be expected such as database unreachable that
 * needs to be started by the user etc.. 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DBSimpleException extends DBException {

	
	private static final long serialVersionUID = 4155203278168533552L;

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
	 *   Original cause
	 */
	public DBSimpleException(DBProperties dbProperties, String msg, SQLException cause) {
		super(dbProperties, msg, cause);
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
	public DBSimpleException(DBProperties dbProperties, String msg) {
		super(dbProperties, msg);
	}

}
