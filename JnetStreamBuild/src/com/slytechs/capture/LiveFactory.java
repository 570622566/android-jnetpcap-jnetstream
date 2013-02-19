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
package com.slytechs.capture;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.LiveCapture;
import org.jnetstream.capture.LiveCaptureDevice;
import org.jnetstream.capture.NetTransmitter;
import org.jnetstream.capture.Captures.LiveCaptureFactory;
import org.jnetstream.filter.Filter;

import com.slytechs.utils.factory.FactoryLoader;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class LiveFactory
    extends FileFactory implements LiveCaptureFactory {
	
	private final static Log logger = LogFactory.getLog(LiveFactory.class);
	
	public static final String LIVE_CAPTURE_FACTORY_CLASS_DEFAULT = 
		"com.slytechs.jnetstream.livecapture.DefaultLiveCaptureFactory";
	
	private static final LiveCaptureFactory local = new FactoryLoader<LiveCaptureFactory>(
	    logger, LIVE_CAPTURE_FACTORY_CLASS_PROPERTY, LIVE_CAPTURE_FACTORY_CLASS_DEFAULT).getFactory();
	
	/**
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#listCaptureDevices()
   */
  public LiveCaptureDevice[] listCaptureDevices() throws IOException {
	  return LiveFactory.local.listCaptureDevices();
  }
	/**
   * @return
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#newCaptureDevice()
   */
  public CaptureDevice newCaptureDevice() {
	  return LiveFactory.local.newCaptureDevice();
  }
	/**
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive()
   */
  public LiveCapture openLive() throws IOException {
	  return LiveFactory.local.openLive();
  }
	/**
   * @param nics
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.capture.CaptureDevice[])
   */
  public LiveCapture openLive(CaptureDevice... nics) throws IOException {
	  return LiveFactory.local.openLive(nics);
  }
	/**
   * @param nics
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(java.util.Collection)
   */
  public LiveCapture openLive(Collection<CaptureDevice> nics)
      throws IOException {
	  return LiveFactory.local.openLive(nics);
  }
	/**
   * @param filter
   * @param nics
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.filter.Filter, org.jnetstream.capture.CaptureDevice[])
   */
  public LiveCapture openLive(Filter filter, CaptureDevice... nics)
      throws IOException {
	  return LiveFactory.local.openLive(filter, nics);
  }
	/**
   * @param filter
   * @param nics
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.filter.Filter, java.util.Collection)
   */
  public LiveCapture openLive(Filter filter, Collection<CaptureDevice> nics)
      throws IOException {
	  return LiveFactory.local.openLive(filter, nics);
  }
	/**
   * @param fiter
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.filter.Filter)
   */
  public LiveCapture openLive(Filter fiter) throws IOException {
	  return LiveFactory.local.openLive(fiter);
  }
	/**
   * @param count
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(long)
   */
  public LiveCapture openLive(long count) throws IOException {
	  return LiveFactory.local.openLive(count);
  }
	/**
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openTransmitter()
   */
  public NetTransmitter openTransmitter() throws IOException {
	  return LiveFactory.local.openTransmitter();
  }
	/**
   * @param netInterface
   * @return
   * @throws IOException
   * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openTransmitter(java.net.NetworkInterface)
   */
  public NetTransmitter openTransmitter(NetworkInterface netInterface)
      throws IOException {
	  return LiveFactory.local.openTransmitter(netInterface);
  }

}
