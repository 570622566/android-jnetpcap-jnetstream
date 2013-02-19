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
package org.jnetstream.filter;

import java.nio.ByteBuffer;

/**
 * A filter used to limit that scope of captures, packet reads from a file or
 * from an IO stream.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Filter<T extends FilterTarget> {

	/**
	 * Takes two filters (left and right) and does an AND logical operation after
	 * evaluating each of the filters.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public final static class AND<T extends FilterTarget> implements Filter<T> {

		private final Filter<T> left;

		private final Filter<T> right;

		/**
		 * 
		 */
		public AND(final Filter<T> left, final Filter<T> right) {
			this.left = left;
			this.right = right;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jnetstream.filter.Filter#accept(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public boolean accept(final ByteBuffer buffer, final T target)
		    throws FilterException {

			return this.left.accept(buffer, target)
			    && this.right.accept(buffer, target);
		}

		/**
		 * Will return the smaller of the evaluated expressions. That is if left
		 * returns 100 and right returns 0 then 0 will be returned.
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return smallest value of the two evaluated filters
		 * @see com.slytechs.capture.filter.Filter#execute(java.nio.ByteBuffer)
		 */
		public long execute(final ByteBuffer buffer, final T target)
		    throws FilterException {
			final long l = this.left.execute(buffer, target);
			final long r = this.right.execute(buffer, target);

			return ((l <= r) ? l : r);
		}

	}

	/**
	 * Returns the larger value either returned by the filter or the supplied max
	 * value. The returned length from {@link #execute} method is guarrantted not
	 * to be smaller then maximum length.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public final static class MAXIMUM<T extends FilterTarget> implements Filter<T> {

		private final Filter<T> filter;

		private final long maximum;

		/**
		 * @param filter
		 *          source filter who's values will be NOTed
		 */
		public MAXIMUM(final Filter<T> filter, final long maximum) {
			this.filter = filter;
			this.maximum = maximum;

		}

		/**
		 * Will always return true unless the supplied maximum is non-zero. If the
		 * supplied maximum is non zero, then no matter what the value from the
		 * source filter is, the non zero maximum value will cause this method to
		 * return true. If the maximum value is 0, then true will be returned if the
		 * evaluated source filter length is non zero. If the evaluted source filter
		 * length was zero and minimum zero, then false would be returned.
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return maximum of the source and the value supplied
		 * @see org.jnetstream.filter.Filter#accept(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public boolean accept(final ByteBuffer buffer, final T target)
		    throws FilterException {

			return execute(buffer, target) != 0;
		}

		/**
		 * The execute method will return the larger of either the evaluated value
		 * from the source filter or the supplied maximum. So if the source filter
		 * returned 100 and the maximum was 10, the value returned would be 100. If
		 * the source filter returned 5 and maximum was 10, then 10 would be
		 * returned.
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return maximum of either the source filter length or value supplied
		 * @see org.jnetstream.filter.Filter#execute(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public long execute(final ByteBuffer buffer, final T target)
		    throws FilterException {

			final long f = filter.execute(buffer, target);

			return (f >= maximum) ? f : maximum;
		}

	}

	/**
	 * Returns the smaller value either returned by the filter or the supplied min
	 * value. The returned length from {@link #execute} method is guarrantted not
	 * to be biffer then min length.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public final static class MINIMUM<T extends FilterTarget> implements Filter<T> {

		private final Filter<T> filter;

		private final long minimum;

		/**
		 * @param filter
		 *          source filter who's values will be NOTed
		 */
		public MINIMUM(final Filter<T> filter, long minimum) {
			this.filter = filter;
			this.minimum = minimum;

		}

		/**
		 * Returns true if evaluted expression was true and false if it was false,
		 * unless minimum value specified was 0 at which time this method will
		 * always return false.
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return minimum of the source and minimum value supplied
		 * @throws FilterException
		 * @see org.jnetstream.filter.Filter#accept(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public boolean accept(final ByteBuffer buffer, final T target)
		    throws FilterException {

			return execute(buffer, target) != 0;
		}

		/**
		 * The execute method will return the smaller of either the evaluated value
		 * from the source filter or the supplied minimum. So if the source filter
		 * returned 100 and the minimum was 10, the value returned would be 10. If
		 * the source filter returned 5 and minimum was 10, then 5 would be
		 * returned.
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return minimum of either the source filter length or value supplied
		 * @see org.jnetstream.filter.Filter#execute(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public long execute(final ByteBuffer buffer, final T target)
		    throws FilterException {

			final long f = filter.execute(buffer, target);

			return (f <= minimum) ? f : minimum;
		}

	}

	/**
	 * Inverts the values returned by a source filter.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public final static class NOT<T extends FilterTarget> implements Filter<T> {

		private final Filter<T> filter;

		/**
		 * @param filter
		 *          source filter who's values will be NOTed
		 */
		public NOT(final Filter<T> filter) {
			this.filter = filter;

		}

		/**
		 * Value of the source filter is inverted. If the source filter returns true
		 * this <code>accept()</code> will return false. If the source filter
		 * returns false, this <code>accept()</code> will return true;
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return opposite of boolean value returned from source filter
		 * @throws FilterException
		 * @see org.jnetstream.filter.Filter#accept(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public boolean accept(final ByteBuffer buffer, final T target)
		    throws FilterException {

			return !filter.accept(buffer, target);
		}

		/**
		 * The execute method will invert the returned length to either
		 * Long.MAX_VALUE or 0. If the source filter returns a length of 0 the value
		 * is inverted and returned by <code>accept()</code> as Long.MAX_VALUE. If
		 * the source filter returned a non zero value, a zero will be returned.
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return inverted length
		 * @see org.jnetstream.filter.Filter#execute(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public long execute(final ByteBuffer buffer, final T target)
		    throws FilterException {

			final long f = filter.execute(buffer, target);

			return (f == 0) ? Long.MAX_VALUE : 0;
		}

	}

	/**
	 * A filter used to limit that scope of captures, packet reads from a file or
	 * from an IO stream.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public final static class OR<T extends FilterTarget> implements Filter<T> {
		private final Filter<T> left;

		private final Filter<T> right;

		public OR(final Filter<T> left, final Filter<T> right) {
			this.left = left;
			this.right = right;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jnetstream.filter.Filter#accept(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public boolean accept(final ByteBuffer buffer, final T target)
		    throws FilterException {

			return this.left.accept(buffer, target)
			    || this.right.accept(buffer, target);
		}

		/**
		 * Will return the bigger of the evaluated expressions. That is if left
		 * returns 100 and right returns 0 then 100 will be returned.
		 * 
		 * @param buffer
		 *          buffer to execute on
		 * @param target
		 *          the target specific filter
		 * @return largest value of the two evaluated filters
		 * @see com.slytechs.capture.filter.Filter#execute(java.nio.ByteBuffer)
		 */
		public long execute(final ByteBuffer buffer, final T target)
		    throws FilterException {
			final long l = this.left.execute(buffer, target);
			final long r = this.right.execute(buffer, target);

			return ((l <= r) ? l : r);
		}
	}

	public final static Filter<?> TRUE = new Filter<FilterTarget>() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jnetstream.filter.Filter#accept(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public boolean accept(ByteBuffer buffer, FilterTarget target)
		    throws FilterException {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jnetstream.filter.Filter#execute(java.nio.ByteBuffer,
		 *      org.jnetstream.filter.FilterTarget)
		 */
		public long execute(ByteBuffer buffer, FilterTarget target)
		    throws FilterException {
			return Long.MAX_VALUE;
		}

	};

	public boolean accept(ByteBuffer buffer, T target)
	    throws FilterException;

	/**
	 * Executes the given filter and returns the result of the evaluation against
	 * the buffer. True means the filter matched, false means it failed.
	 * 
	 * @param buffer
	 *          buffer to execute the filter against
	 * @param target
	 *          Filter target of this filter. This is typically the DLT (Data Link
	 *          Type) of the first structure in the buffer.
	 * @return true filter succeeded, false failed
	 * @throws FilterException
	 *           TODO
	 */
	public long execute(ByteBuffer buffer, T target)
	    throws FilterException;

}
