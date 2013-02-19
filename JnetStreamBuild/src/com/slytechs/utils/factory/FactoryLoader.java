/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package com.slytechs.utils.factory;

import java.util.Formatter;

import org.apache.commons.logging.Log;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class FactoryLoader<T> {

	private final Log logger;
	
	private final String property;
	
	private final String defaults;

	private T factory;

	/**
   * @param logger TODO
	 * @param property
	 * @param defaults
   */
  public FactoryLoader(Log logger, final String property, final String defaults) {
	  this.logger = logger;
		this.property = property;
	  this.defaults = defaults;
	  
	  loadFactoryClass();
  }

	@SuppressWarnings("unchecked")
  public void loadFactoryClass() {
		String path = System.getProperty(property, defaults);

		try {
			factory = (T) Class.forName(path).newInstance();

		} catch (final Exception e) {

			final Formatter out = new Formatter();

			logger.error(out.format("Unable to load factory class [%s]", path)
			    .toString());

//			throw new IllegalStateException(e);
		}
	}

	public T getFactory() {
		return factory;
	}

}
