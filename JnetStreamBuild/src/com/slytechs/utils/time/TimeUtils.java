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
package com.slytechs.utils.time;

import java.util.Calendar;

/**
 * Various time utility methods.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public final  class TimeUtils {
	
	/**
	 * Enum for manipulating time at intervals. Constants are defined to preset 
	 * amount of time each interval takes up in millis() and round*() methods are provided
	 * to round up and down to needed intervals.
	 * 
	 * All methods are thread safe and use a fast non-locking mechanism.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Interval {
		NOW                  ( 1, "" ),
		ONE_MILLI_SECOND     ( 1, "1ms" ),
		TWO_MILLI_SECONDS    ( 2, ONE_MILLI_SECOND, "2ms" ),
		FIVE_MILLI_SECONDS   ( 5, ONE_MILLI_SECOND, "5ms" ),
		HALF_SECOND          ( 500, ONE_MILLI_SECOND, "500ms" ),
		ONE_SECOND           ( 1000, ONE_MILLI_SECOND, "1s" ),
		TWO_SECONDS          ( 2, ONE_SECOND,  "2s" ),
		FIVE_SECONDS         ( 5, ONE_SECOND,  "5s" ),
		TEN_SECONDS          ( 10, ONE_SECOND, "10s" ),
		FIFTEEN_SECONDS      ( 15, ONE_SECOND, "15s" ),
		THIRTY_SECONDS       ( 30, ONE_SECOND, "30s" ),
		FOURTY_FIVE_SECONDS  ( 45 , ONE_SECOND, "45s" ),
		ONE_MINUTE           ( 60, ONE_SECOND, "1m" ),
		TWO_MINUTES          ( 2, ONE_MINUTE,  "2m" ),
		FIVE_MINUTES         ( 5, ONE_MINUTE,  "5m" ),
		TEN_MINUTES          ( 10, ONE_MINUTE, "10m" ),
		FIFTEEN_MINUTES      ( 15, ONE_MINUTE, "15m" ),
		THIRTY_MINUTES       ( 30, ONE_MINUTE, "30m" ),
		ONE_HOUR             ( 60, ONE_MINUTE, "1h" ),
		ONE_HOUR_AND_HALF    ( 90, ONE_MINUTE, "1.5h" ),
		TWO_HOURS            ( 2, ONE_HOUR,   "2h"   ),
		THREE_HOURS          ( 3, ONE_HOUR,   "3h"   ),
		FOUR_HOURS           ( 4, ONE_HOUR,   "4h"   ),
		FIVE_HOURS           ( 5, ONE_HOUR,   "5h"   ),
		SIX_HOURS            ( 6, ONE_HOUR,   "6h"   ),
		SEVEN_HOURS          ( 7, ONE_HOUR,   "7h"   ),
		EIGHT_HOURS          ( 8, ONE_HOUR,   "8h"   ),
		NINE_HOURS           ( 9, ONE_HOUR,   "9h"   ),
		TEN_HOURS            ( 10, ONE_HOUR,  "10h"   ),
		ELEVEN_HOURS         ( 11, ONE_HOUR,  "11h"   ),
		TWELVE_HOURS         ( 12, ONE_HOUR,  "12h"   ),
		THIRTEEN_HOURS       ( 13, ONE_HOUR,  "13h"   ),
		FOURTEEN_HOURS       ( 14, ONE_HOUR,   "14h"   ),
		FIFTEEN_HOURS        ( 15, ONE_HOUR,  "15h"   ),
		SIXTEEN_HOURS        ( 16, ONE_HOUR,   "16h"   ),
		SEVENTEEN_HOURS      ( 17, ONE_HOUR,   "17h"   ),
		EIGHTEEN_HOURS       ( 18, ONE_HOUR,   "18h"   ),
		ONE_DAY              ( 24, ONE_HOUR,   "1d"   ),
		TWO_DAYS             ( 2, ONE_DAY,    "2d"    ),
		THREE_DAYS           ( 3, ONE_DAY,    "3d"    ),
		FOUR_DAYS            ( 4, ONE_DAY,    "4d"    ),
		FIVE_DAYS            ( 5, ONE_DAY,    "5d"    ),
		SIX_DAYS             ( 6, ONE_DAY,    "6d"    ),
		SEVEN_DAYS           ( 7, ONE_DAY,    "7d"    ),
		FIFEEN_DAYS          ( 15, ONE_DAY,    "15d"    ),
		THIRTY_DAYS          ( 30, ONE_DAY,    "30d"    ),
		ONE_WEEK             ( 1, SEVEN_DAYS,  "1w"    ),
		TWO_WEEKS            ( 2, ONE_WEEK,    "2w"    ),
		THREE_WEEKS          ( 3, ONE_WEEK,    "3w"    ),
		FOUR_WEEKS           ( 4, ONE_WEEK,    "4w"    ),
		FIVE_WEEKS           ( 5, ONE_WEEK,    "5w"    ),
		SIZE_WEEKS           ( 6, ONE_WEEK,    "6w"    ),
		SEVEN_WEEKS          ( 7, ONE_WEEK,    "7w"    ),
		EIGHT_WEEKS          ( 8, ONE_WEEK,    "8w"    ),
		NINE_WEEKS           ( 9, ONE_WEEK,    "9w"    ),
		TEN_WEEKS            ( 10, ONE_WEEK,    "10w"    ),
		ELEVEN_WEEKS         ( 11, ONE_WEEK,    "11w"    ),
		TWELVE_WEEKS         ( 12, ONE_WEEK,    "12w"    ),
		
		/*
		 * The enums aren't constant therefore millis() are estimated based on average
		 * But the roundDown() and roundUp() methods are calculated using a calendar
		 * so they are accurate.
		 */
		ONE_MONTH            ( 1, THIRTY_DAYS, "1mo" ), 
		TWO_MONTHS           ( 2, ONE_MONTH, "2mo" ), 
		THREE_MONTHS         ( 3, ONE_MONTH, "3mo" ), 
		FOUR_MONTHS          ( 4, ONE_MONTH, "4mo" ), 
		FIVE_MONTHS          ( 5, ONE_MONTH, "5mo" ), 
		SIX_MONTHS           ( 6, ONE_MONTH, "6mo" ),
		ONE_QUARTER          ( 1, TWELVE_WEEKS, "1q" ),
		TWO_QUARTERS         ( 2, ONE_QUARTER, "2q" ),
		THREE_QUARTERS       ( 3, ONE_QUARTER, "3q" ),
		FOUR_QUARTERS        ( 4, ONE_QUARTER, "4q" ),
		FIVE_QUARTERS        ( 5, ONE_QUARTER, "5q" ),
		ONE_YEAR             ( 1, FOUR_QUARTERS, "1y" ),
		TWO_YEARS            ( 2, ONE_YEAR, "2y" ),
		THREE_YEARS          ( 3, ONE_YEAR, "3y" ),
		FOUR_YEARS           ( 4, ONE_YEAR, "4y" ),
		FIVE_YEARS           ( 5, ONE_YEAR, "5y" ),
		TEN_YEARS            ( 10, ONE_YEAR, "10y" ),
		ONE_DECADE           ( 1, TEN_YEARS, "1de" ),
		TWO_DECADES          ( 2, ONE_DECADE, "2de" ),
		THREE_DECADES        ( 3, ONE_DECADE, "3de" ),
		TEN_DECADES          ( 10, ONE_DECADE, "10de" ),
		ONE_CENTURY          ( 1, TEN_DECADES, "1ce" ),
		TWO_CENTURIES        ( 2, TEN_DECADES, "2ce" ),
		TEN_CENTURIES        ( 10, TEN_DECADES, "10ce" ),
		ONE_MILLENIUM        ( 1, TEN_CENTURIES, "1mi" ),
		TWO_MILLENIUMS       ( 2, ONE_MILLENIUM, "2mi" ),
		THREE_MILLENIUMS     ( 3, ONE_MILLENIUM, "3mi" ),
		
		;

		/**
		 * Create a thread safe calendar. This avoids having to synchronize with
		 * all methods that need to use calendar.
		 */
		private ThreadLocal<Calendar> calendar = new ThreadLocal<Calendar>() { 
			protected Calendar initialValue() {
				return Calendar.getInstance();
			}
		};
	    		
		private final long millis;
		
		private final Interval multiple;
		
		private final int count;

		private final String abbr;
		
		Interval(final int count, final String abbr){
			this.count = count;
			this.abbr = abbr;
			this.multiple = null;
			
			this.millis = 1;
			
		}
		
		Interval(final int count, final Interval multiple, final String abbr){
			this.count = count;
			this.multiple = multiple;
			this.abbr = abbr;
			
			this.millis = count * multiple.millis;
			
		}
		
		/**
		 * <P>Returns the number of milli seconds for the given interval using the current time.
		 * That is most intervals have constant number of milli seconds, but intervals starting 
		 * with MONTH and up vary in number of milli seconds depending on the date. 
		 * </P><P>
		 * The millis returned is acurate for the current interval within current timeframe. i.e.
		 * the interval ONE_MONTH in the month of february will have different amount of millis during
		 * the leap year then any non leap year. Also ONE_MONTH will typically have a different number of
		 * millis for every month depending on the number of day's within that month. Same principle applies
		 * to months, quarters, years, decades, centuries and milleniums.
		 * </P>
		 * @return
		 */
		public final long millis() {
			return roundUp() - roundDown();
		}
		
		public final long seconds() {
			return millis / 1000;
		}
		
		public final long micros() {
			return millis * 1000;
		}

		
		public final long nanos() {
			return millis * 1000000;
		}

		
		/**
		 * The abbreviation used to abbriviate this interval, (i.e. ms for milliseconds, etc..)
		 * @return
		 *   Abbreviation for this interval.
		 */
		public final String abbr() {
			return abbr;
		}
		
		/**
		 * Round off from the current system time to the nearest lowest interval.
		 * i.e. ONE_MINUTE will roll back to the beginning of the previous minute.
		 * 
		 * @return rounded off time to the interval in millis.
		 */
		public long roundDown() {
			return roundDown(System.currentTimeMillis());
		}
		
		/**
		 * Round off the specified time to the nearest lowest interval.
		 * i.e. ONE_MINUTE will roll back to the beginning of the previous minute.
		 * 
		 * @param time a time interval in milli-seconds.
		 * @return rounded off time to the interval in millis.
		 */
		public long roundDown(long time) {

			long t = 0;
			long tm = 0; // Helper temp variable

			/* Retrieve thread safe calendar instance. */
			Calendar cal = calendar.get();
			cal.setTimeInMillis(time);

			if (this.multiple == null) {
				switch (this) {
				
				case ONE_MILLI_SECOND:
				case NOW:
					break;

				default:
					throw new RuntimeException("Invalid interval " + toString());
				}
			} else {
				switch (this.multiple) {
				
				case TEN_CENTURIES:
				case ONE_MILLENIUM:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_YEAR, 1);
					cal.add(Calendar.YEAR, - cal.get(Calendar.YEAR) % 1000);
					
					cal.add(Calendar.YEAR, - (this.count - 1) * 1000);
					
					break;

				case TEN_DECADES:
				case ONE_CENTURY:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_YEAR, 1);
					cal.add(Calendar.YEAR, - cal.get(Calendar.YEAR) % 100);
					
					cal.add(Calendar.YEAR, - (this.count - 1) * 100);
					
					break;

				case TEN_YEARS:
				case ONE_DECADE:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_YEAR, 1);
					cal.add(Calendar.YEAR, - cal.get(Calendar.YEAR) % 10);
					
					cal.add(Calendar.YEAR, - (this.count - 1) * 10);
					
					break;
				
				case FOUR_QUARTERS:
				case ONE_YEAR:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_YEAR, 1);
					
					cal.add(Calendar.YEAR, - (this.count - 1));
					
					break;
			
				case TWELVE_WEEKS:
				case ONE_QUARTER:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_MONTH, 0);
					
					/*
					 * Round down by 1 quarter
					 */
					int m = cal.get(Calendar.MONTH);
					int q = m / 4;
					m = q * 4;
					cal.set(Calendar.MONTH, m);
					
					/*
					 * Now subtract N moths per additional quarter we need
					 * to go back.
					 */
					m = - (this.count -1) * 3;
					cal.add(Calendar.MONTH, m);
					
					break;
					
				case THIRTY_DAYS:
				case ONE_MONTH:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					
					cal.add(Calendar.MONTH, - (this.count - 1));
					
					break;

					
				case SEVEN_DAYS:
				case ONE_WEEK:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

					tm = cal.getTimeInMillis();
					
					tm -= (this.millis() - ONE_WEEK.millis);
					cal.setTimeInMillis(tm);
//					tm = ((time - cal.getTimeInMillis()) / this.millis());
//					cal.set(Calendar.WEEK_OF_YEAR, (int) tm * this.count + 1);
					break;

				case ONE_DAY:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_MONTH, 1);

					tm = ((time - cal.getTimeInMillis()) / this.millis);
					cal.set(Calendar.DAY_OF_MONTH, (int) tm * this.count + 1);
					break;

				case ONE_HOUR:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);

					tm = ((time - cal.getTimeInMillis()) / this.millis);
					cal.set(Calendar.HOUR_OF_DAY, (int) tm * this.count);
					break;

				case ONE_MINUTE:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);

					tm = ((time - cal.getTimeInMillis()) / millis);
					cal.set(Calendar.MINUTE, (int) tm * this.count);
					break;

				case ONE_SECOND:

					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);

					tm = ((time - cal.getTimeInMillis()) / millis);
					cal.set(Calendar.SECOND, (int) tm * this.count);
					break;

				case ONE_MILLI_SECOND:

					cal.set(Calendar.MILLISECOND, 0);

					tm = ((time - cal.getTimeInMillis()) / millis());
					cal.set(Calendar.MILLISECOND, (int) tm * this.count);
					break;

				}
			}
			t = cal.getTimeInMillis();

			return t;

		}
		
		/**
		 * Round up the specified time to the nearest higher interval. i.e.
		 * ONE_MINUTE will roll up to the beginning of the next minute.
		 * 
		 * @return rounded up time from the current system time
		 */
		public long roundUp() {
			return roundUp(System.currentTimeMillis());
		}

		/**
		 * Round off the specified time to the nearest lowest interval.
		 * i.e. ONE_MINUTE will roll back to the beginning of the previous minute.
		 * 
		 * @param time a time interval in milli-seconds.
		 * @return rounded off time to the interval in millis.
		 */
		public long roundUp(long time) {

			long t = 0;

			/* Retrieve thread safe calendar instance. */
			Calendar cal = calendar.get();
			cal.setTimeInMillis(time);

			if (this.multiple == null) {
				switch (this) {
				
				case ONE_MILLI_SECOND:
				case NOW:
					return time;

				default:
					throw new RuntimeException("Invalid interval " + toString());
				}
			}

			switch (this.multiple) {

			case TEN_CENTURIES:
			case ONE_MILLENIUM:

				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				cal.add(Calendar.YEAR, 1000 -cal.get(Calendar.YEAR) % 1000);

				break;

			case TEN_DECADES:
			case ONE_CENTURY:

				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				cal.add(Calendar.YEAR, 100 -cal.get(Calendar.YEAR) % 100);

				break;

			case TEN_YEARS:
			case ONE_DECADE:

				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				cal.add(Calendar.YEAR, 10 - cal.get(Calendar.YEAR) % 10);

				break;

			case FOUR_QUARTERS:
			case ONE_YEAR:

				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_YEAR, 1);

				cal.add(Calendar.YEAR, 1);

				break;

			case TWELVE_WEEKS:
			case ONE_QUARTER:

				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_MONTH, 0);

				/*
				 * Round down by 1 quarter
				 */
				int m = cal.get(Calendar.MONTH);
				int q = m / 4;
				m = q * 4;
				cal.set(Calendar.MONTH, m);

				/*
				 * Now subtract N moths per additional quarter we need to go
				 * back.
				 */
				cal.add(Calendar.MONTH, 3);

				break;

			case THIRTY_DAYS:
			case ONE_MONTH:

				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_MONTH, 1);

				cal.add(Calendar.MONTH, 1);

				break;

			case SEVEN_DAYS:
			case ONE_WEEK:

				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				cal.add(Calendar.DAY_OF_MONTH, 7);

				break;

			/*
			 * The constant ones we can easily calculate
			 */
			case ONE_DAY:
			case ONE_HOUR:
			case ONE_MINUTE:
			case ONE_SECOND:
			case ONE_MILLI_SECOND:
				return roundDown(time) + this.millis;

			}

			t = cal.getTimeInMillis();

			return t;

		}
	}
}
