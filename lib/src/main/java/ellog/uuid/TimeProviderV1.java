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
import java.time.Duration;
import java.time.Instant;

/**
 * This class is a time provider which is used to timestamps for use in UUIDs with version 1.
 *
 * This provider remembers the last milliseconds timestamp and adds a fixed amount of nanoseconds in case of a collision.
 * The timestamps which are generated are guaranteed to be unique, even if the system clock does not provide sufficient granularity.
 */
public class TimeProviderV1 {

	/**
	 * Reference date (start of gregorian calendar) for UUID version 1.
	 */
	public static final Instant REFERENCE = Instant.parse("1582-10-15T00:00:00Z");

	/**
	 * The last instant emitted, or the mimimal possible value if none was emitted yet.
	 */
	protected Instant lastInstant = Instant.MIN;
	/**
	 * The clock used to get the current time.
	 */
	protected Clock clock = Clock.systemUTC();

	/**
	 * Create a new instance of this class.
	 */
	protected TimeProviderV1() {
	}

	/**
	 * Create a new synchronized instance of this class.
	 * @return The new instance.
	 */
	public static TimeProviderV1 create() {
		return create(true);
	}

	/**
	 * Create a new instance of this class.
	 * @param withLock Whether to synchronize access to the instance.
	 * @return The new instance.
	 */
	public static TimeProviderV1 create(boolean withLock) {
		if (withLock) {
			return new TimeProviderV1Locked();
		} else {
			return new TimeProviderV1();
		}
	}

	private static class TimeProviderV1Locked extends TimeProviderV1 {
		@Override
		public synchronized Instant getNext(long nanoPrecision) {
			return super.getNext(nanoPrecision);
		}
	}

	/**
	 * Set the clock used to get the current time.
	 * @param clock The new clock.
	 * @return This instance for method chaining.
	 */
	public TimeProviderV1 setClock(Clock clock) {
		this.clock = clock;
		return this;
	}

	/**
	 * Get the next unique timestamp.
	 * @param nanoPrecision Amount of nanoseconds to add in case of a collision.
	 * @return The next timestamp.
	 */
	public Instant getNext(long nanoPrecision) {
		Instant nextInstant = clock.instant();
		if (nextInstant.isAfter(lastInstant)) {
			lastInstant = nextInstant;
			return nextInstant;
		} else {
			lastInstant = lastInstant.plusNanos(nanoPrecision);
			return lastInstant;
		}
	}

	/**
	 * Get the next unique timestamp with the specified precision starting at the reference time.
	 * @param nanoPrecision Amount of nanoseconds in the timestamp.
	 * @return The next timestamp.
	 * @see #REFERENCE
	 */
	public long getNextRefTimestamp(long nanoPrecision) {
		Instant next = getNext(nanoPrecision);
		Duration interval = Duration.between(REFERENCE, next);
		long seconds = interval.getSeconds() * (1_000_000_000 / nanoPrecision);
		long nanos = interval.getNano() / nanoPrecision;
		long result = seconds + nanos;
		return result;
	}

	/**
	 * Get the next unique timestamp with 100ns precision starting at the reference time.
	 * @return The next timestamp.
	 * @see #REFERENCE
	 */
	public long getNextRefTimestamp100ns() {
		return getNextRefTimestamp(100);
	}
}
