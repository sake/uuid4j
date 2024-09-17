/*
 * Copyright © 2023 Tobias Wich
 * This file is part of the electrologic UUID library.
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <https://www.gnu.org/licenses/>.
 */

package ellog.uuid;

import java.time.Clock;

/**
 * This class is a time provider which is used to timestamps for use in UUIDs with version 7.
 *
 * The output of a value is guaranteed to be monotonically increasing, even if the system clock does not provide sufficient granularity.
 * This is achieved by providing a collision counter value in addition to the epoch timestamp value.
 * It is the obligation of the UUID supplier to reserve enough bits for the counter value and take care of overflows.
 */
public class TimeProviderV7 {

	/**
	 * The last milliseconds timestamp emitted, or the minimal possible value if none was emitted yet.
	 */
	protected long lastMillis = 0;
	/**
	 * The last counter value emitted, or the minimal possible value if none was emitted yet.
	 */
	protected int counter = 0;
	/**
	 * The clock used to get the current time.
	 */
	protected Clock clock = Clock.systemUTC();

	/**
	 * Create a new instance of this class.
	 */
	protected TimeProviderV7() {
	}

	/**
	 * Create a new synchronized instance of this class.
	 * @return The new instance.
	 */
	public static TimeProviderV7 create() {
		return create(true);
	}

	/**
	 * Create a new instance of this class.
	 * @param withLock Whether to synchronize access to the instance.
	 * @return The new instance.
	 */
	public static TimeProviderV7 create(boolean withLock) {
		if (withLock) {
			return new TimeProviderV7Locked();
		} else {
			return new TimeProviderV7();
		}
	}

	private static class TimeProviderV7Locked extends TimeProviderV7 {
		@Override
		public synchronized TimeAndCounter getNext() {
			return super.getNext();
		}
	}

	/**
	 * A data class for a timestamp and a counter value.
	 */
	public static class TimeAndCounter {
		/** The timestamp value. */
		public final long time;
		/** The counter value. */
		public final int counter;

		/**
		 * Create a new instance of this class.
		 * @param time The timestamp value.
		 * @param counter The counter value.
		 */
		public TimeAndCounter(long time, int counter) {
			this.time = time;
			this.counter = counter;
		}
		/**
		 * Get the number of bits required to store the counter value.
		 * @return The number of bits required to store the counter value.
		 */
		public int numCounterBits() {
			return Integer.SIZE - Integer.numberOfLeadingZeros(counter);
		}
	}

	/**
	 * Get the next unique and monotonically increased timestamp and counter value.
	 * @return The next timestamp and counter value.
	 */
	public TimeAndCounter getNext() {
		long millis = clock.millis();
		if (millis > lastMillis) {
			lastMillis = millis;
			counter = 0;
		} else {
			counter++;
		}
		return new TimeAndCounter(lastMillis, counter);
	}

}
