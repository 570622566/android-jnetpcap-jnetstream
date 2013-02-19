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
package com.slytechs.utils.io;

import java.io.IOException;
import java.io.OutputStream;

import com.slytechs.utils.time.TimeUtils.Interval;

/**
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RateLimitedOutputStream extends OutputStream {
	
	public final static long KILO = 1024;
	public final static long MEGA = KILO * KILO;
	public final static long GIGA = MEGA * KILO;
	public final static long TERA = GIGA * KILO;
	public final static long PETA = TERA * KILO;
	public final static long FEMTA = PETA * KILO;
	
	public enum RateLimits {
		OneKilo					(1),
		TwoKilo					(2),
		ThreeKilo				(3),
		FourKilo				(4),
		FiveKilo				(5),
		SixKilo					(6),
		SevenKilo				(7),
		EightKilo				(8),
		NineKilo				(9),
		TenKilo					(10),
		FourteenFourKilo		(14.4),
		TwentyEightEightKilo	(28.8),
		ThirtyEithtFourKilo		(28.4),
		FiftySixKilo			(56),
		OneTwelveKilo			(112),
		OneTwentyEightKilo		(128),
		TwoFiftySixKilo			(256),
		ThreeSixtyFoundKilo		(364),
		FiveTwelveKilo			(512),
		SevenSixtyEithtKilo		(768),
		OneMega					(KILO),
		OneAndHalfMeg			(1.5 * KILO),
		TwoMeg					(2 * KILO),
		ThreeMeg				(3 * KILO),
		FiveMeg					(5 * KILO),
		TenMeg					(10 * KILO),
		TwentyMeg				(20 * KILO),
		ThirtyMeg				(30 * KILO),
		FourtyMeg				(40 * KILO),
		FourtyFiveMeg			(45 * KILO),
		FiftyMegs				(50 * KILO),
		NineTyMegs				(90 * KILO),
		OneHundredMegs			(100 * KILO),
		OneFiftyFiveMeg			(155 * KILO),
		FiveTwelveMeg			(512 * KILO),
		OneGig					(MEGA),
		TwoGig					( 2 * MEGA),
		FiveGig					(5 * MEGA),
		TenGig					(10 * MEGA),
		TwentyGig				(20 * MEGA),
		OneTera					(GIGA),
		OnePeta					(TERA),
		OneFemta				(PETA),

		;
		
		private final long rate;
		

		private RateLimits(double rate) {
			this.rate =  (long) (rate * KILO);
			
		}

		/**
		 * Returns the rate as 
		 * @return
		 */
		public long getRate() {
			return rate;
		}
	}
	
	/**
	 * Rate limit imposed by the user. Default is One Femta (10^18).
	 */
	private long rateLimit = RateLimits.OneFemta.getRate();
	
	/**
	 * Maximum bytes allowed to be written within rate limit interval
	 * and the rate limits set by the user.
	 */
	private long maxBytes = 0;
	
	/**
	 * Sample period for which we calculate rates
	 * 
	 * Default is 512ms, or 1/2 second which is beyond the average WAN link latency
	 * of 100 - 300ms.
	 */
	private Interval sampleInterval = Interval.ONE_SECOND;
	
	/**
	 * When the last sample timeframe was recorded.
	 */
	private long lastSample = 0;

	/**
	 * Count of bytes written to the stream.
	 */
	private long count = 0;
	
	/**
	 * Statistic about averageRate for bytes written to the stream.
	 */
	private double averageRate = 0;
	
	/**
	 * Statistic about maximum rate that bytes were written within a 
	 * sample timeframe
	 */
	private double maxRate = 0;
	
	private final OutputStream out;
	
	public RateLimitedOutputStream(OutputStream out) {
		this.out = out;
	}
	
	public RateLimitedOutputStream(OutputStream out, long rateLimit) {
		this.out = out;
		setRateLimit(rateLimit);
	}
	
	public RateLimitedOutputStream(OutputStream out, RateLimits limit) {
		this.out = out;
		setRateLimit(limit.getRate());
	}
	
	public RateLimitedOutputStream(OutputStream out, RateLimits limit, Interval sampleInterval) {
		this.out = out;
		setRateLimit(limit.getRate());
		setSampleInterval(sampleInterval);
	}
	

    /**
     * Writes <code>b.length</code> bytes from the specified byte array 
     * to this output stream. The general contract for <code>write(b)</code> 
     * is that it should have exactly the same effect as the call 
     * <code>write(b, 0, b.length)</code>.
     * At the same time the stream rate limit is enforced. A block will
     * occur when the rate limit set is exeeded and will resume when 
     * more data can be written. A portion of the buffer may be written
     * while the rest of the buffer will be blocked on. This method
     * returns only when the entire user specified buffer has been written. 
     *
     * @param      b   the data.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte b[]) throws IOException {
    	write(b, 0, b.length);
    }

	
    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to this output stream.
     * At the same time the stream rate limit is enforced. A block will
     * occur when the rate limit set is exeeded and will resume when 
     * more data can be written. A portion of the buffer may be written
     * while the rest of the buffer will be blocked on. This method
     * returns only when the entire user specified buffer has been written. 
     * The general contract for <code>write(b, off, len)</code> is that 
     * some of the bytes in the array <code>b</code> are written to the 
     * output stream in order; element <code>b[off]</code> is the first 
     * byte written and <code>b[off+len-1]</code> is the last byte written 
     * by this operation.
     * <p>
     * The <code>write</code> method of <code>OutputStream</code> calls 
     * the write method of one argument on each of the bytes to be 
     * written out. Subclasses are encouraged to override this method and 
     * provide a more efficient implementation. 
     * <p>
     * If <code>b</code> is <code>null</code>, a 
     * <code>NullPointerException</code> is thrown.
     * <p>
     * If <code>off</code> is negative, or <code>len</code> is negative, or 
     * <code>off+len</code> is greater than the length of the array 
     * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs. In particular, 
     *             an <code>IOException</code> is thrown if the output 
     *             stream is closed.
     */

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		
		if (b == null) {
			throw new NullPointerException();
			
		} else if ((off < 0) || (off > b.length) || (len < 0)
				|| ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
			
		} else if (len == 0) {
			return;
		}		
		
		/*
		 * This loop possibly breaks apart the buffer and writes partial buffers.
		 * As much data as is allowed by the rate limit policy. Most of the time
		 * the entire buffer fits in a single write, especially for larger policies
		 * so the loop is really engaged in corner cases when rate limit is about
		 * to be exhausted. 
		 */
		boolean sanityCheck = true;
		while (true) {
			
			updateSampleInterval();
			
			int l = getBytesRemaining();
			
			if (l == 0) {
				
				/**
				 * Make sure we don't end up in an infinate loop
				 * if some how we can't fit data within the
				 * rate limit policy and this comes up repeatadly.
				 */
				assert(sanityCheck);
				sanityCheck = false;;
				
				blockUntilEndOfInterval();
				continue;
			}
			
			sanityCheck = true;
			
			if (l > len) {
				l = len;
			}
			
			out.write(b, off, l);
			count += l;
			off += l;
			len -= l;
			
			if (len <= 0) {
				break;
			}	
		}		
	}
	
	/**
	 * Number of bytes remaining for this sample interval.
	 * @return Number of bytes that can be written and stay within the ratelimit policy set.
	 */
	private int getBytesRemaining() {
		return (int) (maxBytes - count);
	}

    /**
     * Writes the specified byte to this output stream. The general 
     * contract for <code>write</code> is that one byte is written 
     * to the output stream. The byte to be written is the eight 
     * low-order bits of the argument <code>b</code>. The 24 
     * high-order bits of <code>b</code> are ignored.
     * At the same time the stream rate limit is enforced. A block will
     * occur when the rate limit set is exeeded and will resume when 
     * more data can be written.     
     * <p>
     *
     * @param      b   the <code>byte</code>.
     * @exception  IOException  if an I/O error occurs. In particular, 
     *             an <code>IOException</code> may be thrown if the 
     *             output stream has been closed.
     */
	@Override
	public void write(int b) throws IOException {
		
		updateSampleInterval();
		
		if (getBytesRemaining() == 0) {
			blockUntilEndOfInterval();
		}
		
		out.write(b);
		count ++;
	}
	
	/**
	 * Enforces the rate policy by calculating current rates and makes sure we don't
	 * go over them. It also keeps some statistics about average and max rates.
	 * 
	 * @return True indicates that everything was within policy and no wait time was required
	 * A false indicates that some amount of wait time was imposed on this thread.
	 */
	private void updateSampleInterval() {
		
		long t = sampleInterval.roundDown();
		
		/**
		 * Check if we have a new interval
		 */
		if (lastSample < t) {
			double r = count / (sampleInterval.millis() / 1000);

			if (r > maxRate) {
				maxRate = r;
			}
			
			averageRate = (averageRate + r) / 2;
			
			count = 0;
			lastSample = t;
		}
				
	}
	
	private void blockUntilEndOfInterval() {
		try {
			Thread.sleep(sampleInterval.roundUp() - System.currentTimeMillis());
		} catch (InterruptedException e) {
			/* Empty */
		}
		
	}

	public void close() throws IOException {
		out.close();
	}

	public void flush() throws IOException {
		out.flush();
	}

	public long getRateLimit() {
		return rateLimit;
	}

	public void setRateLimit(long rateLimit) {
		this.rateLimit = rateLimit;
		this.maxBytes = rateLimit * sampleInterval.millis() / 1000;
	}

	public Interval getSampleInterval() {
		return sampleInterval;
	}

	public void setSampleInterval(Interval sampleInterval) {
		this.sampleInterval = sampleInterval;
		this.maxBytes = rateLimit * sampleInterval.millis() / 1000;
	}
	
	public static void main(String[] args) {
		
	}

}
