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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;

import com.slytechs.utils.db.DatabaseUtils.DatabaseType;

/**
 * Properties that are used to connect to a database. With all of their options.
 * 
 * This is a dynamic bean. Setting any parameters here will cause all DB objects using
 * its info update their states including reconnecting to the database using new properties set.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DBProperties {
	
	private Properties properties = new Properties();
	
	private DatabaseType dbType;
	private String dbHost;
	private String dbUser;
	private String dbUserPassword;
	private int dbPort;
	
	private String dbDatabaseName;
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/**
	 * Constant database name property label.
	 */
	public static final String DB_DATABASE_NAME = "dbDatabaseName";

	/**
	 * Constant database port property label.
	 */
	public static final String DB_PORT = "dbPort";

	/**
	 * Constant database host ip or name property label.
	 */
	public static final String DB_HOST = "dbHost";

	/**
	 * Constant database type property label.
	 */
	public static final String DB_TYPE = "dbType";

	/**
	 * Constant database user password property label.
	 */
	public static final String DB_USER_PASSWORD = "dbUserPassword";

	/**
	 * Constant database user name property label.
	 */
	public static final String DB_USER = "dbUser";
	
	/**
	 * Some or all properties changed. Thus event on entire source object is
	 * dispatched.
	 */
	public static final String DB_PROPERTIES = "dbProperties";
	

	/**
	 * Empty constructor for Java Bean style initialization.
	 */
	public DBProperties() {
		/* Empty, according to BEAN pattern */
	}
	
	/**
	 * Creates an initialized properites object that can be used in various
	 * utilities to connect to a database. Does not open up a specific database.
	 * 
	 * @param dbType 
	 *   DatabaseUtils.DatabaseType that masks lots of vendor specific
	 *   initialization stuff.
	 * 
	 * @param dbHost
	 *   Either IP or hostname of the database host.
	 *   
	 * @param dbUser
	 *   User name to use for database connection
	 *   
	 * @param dbUserPassword
	 *   User password for database connection.
	 */
	public DBProperties(DatabaseType dbType, String dbHost, String dbUser, String dbUserPassword) {
		
		setDbType(dbType);
		setDbHost(dbHost);
		setDbUser(dbUser);
		setDbUserPassword(dbUserPassword);
	}
	
	/**
	 * Creates an initialized properites object that can be used in various
	 * utilities to connect to a database. Opens up the specified database.
	 * 
	 * @param dbType 
	 *   DatabaseUtils.DatabaseType that masks lots of vendor specific
	 *   initialization stuff.
	 * 
	 * @param dbHost
	 *   Either IP or hostname of the database host.
	 *   
	 * @param dbUser
	 *   User name to use for database connection
	 *   
	 * @param dbUserPassword
	 *   User password for database connection.
	 *   
	 * @param dbDatabaseName
	 *   Database to connect to.
	 */
	
	public DBProperties(DatabaseType dbType, String dbHost, String dbUser, String dbUserPassword, String dbDatabaseName) {
		
		setDbType(dbType);
		setDbHost(dbHost);
		setDbUser(dbUser);
		setDbUserPassword(dbUserPassword);
		setDbDatabaseName(dbDatabaseName);
	}
	
	/**
	 * Property change dispatcher.
	 */
	public void notifyAllPropertiesChanged() {
		firePropertyChange(DB_PROPERTIES, null, this);
	}

	/**
	 * Converts to regular java.util.Properties.
	 * 
	 * @return
	 *  Converted properties.
	 */
	public Properties getAsJavaProperties() {
		return properties;
	}

	/**
	 * Retries the database hostname or IP from these properties.
	 * 
	 * @return
	 *   Hostname or ip of database host.
	 */
	public String getDbHost() {
		return dbHost;
	}

	/**
	 * Sets the database hostname or ip address in these properties.
	 * 
	 * @param dbHost
	 *   Hostname to set.
	 */
	public void setDbHost(String dbHost) {
		String oldValue = this.dbHost;
		this.dbHost = dbHost;
		
		properties.setProperty(DB_HOST, dbHost);
		
		firePropertyChange(DB_HOST, oldValue, dbHost);
	}

	/**
	 * The port number defined in these properites.
	 * 
	 * @return
	 *   port number.
	 */
	public int getDbPort() {
		return dbPort;
	}

	/**
	 * Sets the port number to use for connection to the database.
	 * 
	 * @param dbPort
	 *   The port number for the database.
	 */
	public void setDbPort(int dbPort) {
		int oldValue = this.dbPort;
		this.dbPort = dbPort;

		properties.put(DB_PORT, dbPort);

		firePropertyChange(DB_PORT, oldValue, dbPort);
	}

	/**
	 * Returns the specific database type as defined in these
	 * properties.
	 * 
	 * @return
	 *   DatabaseType as defined in these properties.
	 */
	public DatabaseType getDbType() {
		return dbType;
	}

	/**
	 * Sets the specific database type in these properties. Any property
	 * change listeners registed will be notified of the change and database
	 * may reconnect.
	 * 
	 * @param dbType
	 *   The database type to set.
	 */
	public void setDbType(DatabaseType dbType) {
		DatabaseType oldValue = this.dbType;
		this.dbType = dbType;
		
		properties.put(DB_TYPE, dbType);
		
		firePropertyChange(DBProperties.DB_TYPE, oldValue, dbType);
	}

	/**
	 * Gets the user name as defined in these properties.
	 * 
	 * @return
	 *   User name.
	 */
	public String getDbUser() {
		return dbUser;
	}

	/**
	 * Sets the user name and notifies all the listeners.
	 * 
	 * @param dbUser
	 *   User name to set.
	 */
	public void setDbUser(String dbUser) {
		String oldValue = this.dbUser;
		this.dbUser = dbUser;

		properties.put(DB_USER, dbUser);

		firePropertyChange(DBProperties.DB_USER, oldValue, dbUser);
	}

	/**
	 * Gets the password used to connect to the database. 
	 * 
	 * @return
	 *   User password used for database connections.
	 */
	public String getDbUserPassword() {
		return dbUserPassword;
	}

	/**
	 * Sets the new database password. Notifies all the listeners of the
	 * change. Has no effect on the actual database password in the database.
	 * 
	 * @param dbUserPassword
	 *   User password to set these properties to.
	 */
	public void setDbUserPassword(String dbUserPassword) {
		String oldValue = this.dbUserPassword;
		this.dbUserPassword = dbUserPassword;
		
		properties.put(DB_USER_PASSWORD, dbUserPassword);

		firePropertyChange(DBProperties.DB_USER_PASSWORD, oldValue, dbUserPassword);
	}

	/**
	 * Adds a new property change listener for any changes to these properties.
	 * 
	 * @param name
	 *   Property name.
	 *   
	 * @param listener
	 *   Listener to add.
	 */
	public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(name, listener);
	}

	/**
	 * Fires property change to all the listeners.
	 * 
	 * @param name 
	 *   Property name.
	 *   
	 * @param oldValue
	 *   Old property value.
	 *   
	 * @param newValue
	 *   New property value.
	 */
	public void firePropertyChange(String name, Object oldValue, Object newValue) {
		listeners.firePropertyChange(name, oldValue, newValue);
	}

	/**
	 * Removes the specified listener from the notification list for this property.
	 * 
	 * @param name
	 *   Property name.
	 *   
	 * @param listener
	 *   Listener to remove.
	 */
	public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(name, listener);
	}

	/**
	 * Returns the database name if one was set.
	 * 
	 * @return
	 *   Database if one was set. Null if one was not set.
	 */
	public String getDbDatabaseName() {
		return dbDatabaseName;
	}

	/**
	 * Sets the database name. And notifies all the listeners.
	 * 
	 * @param dbDatabaseName
	 *   Database name.
	 */
	public void setDbDatabaseName(String dbDatabaseName) {
		String oldValue = this.dbDatabaseName;
		this.dbDatabaseName = dbDatabaseName;
		
		properties.put(DB_DATABASE_NAME, dbDatabaseName);

		firePropertyChange(DBProperties.DB_DATABASE_NAME, oldValue, dbDatabaseName);
	}

	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		listeners.addPropertyChangeListener(arg0);
	}

}
