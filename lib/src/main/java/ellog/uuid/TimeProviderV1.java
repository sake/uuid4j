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

public class TimeProviderV1 {

	public static final Instant REFERENCE = Instant.parse("1582-10-15T00:00:00Z");

	protected Instant lastInstant = Instant.MIN;
	protected Clock clock = Clock.systemUTC();

	protected TimeProviderV1() {
	}

	public static TimeProviderV1 create() {
		return create(true);
	}
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

	public TimeProviderV1 setClock(Clock clock) {
		this.clock = clock;
		return this;
	}

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

	public long getNextRefTimestamp(long nanoPrecision) {
		Instant next = getNext(nanoPrecision);
		Duration interval = Duration.between(REFERENCE, next);
		long seconds = interval.getSeconds() * (1_000_000_000 / nanoPrecision);
		long nanos = interval.getNano() / nanoPrecision;
		long result = seconds + nanos;
		return result;
	}

	public long getNextRefTimestamp100ns() {
		return getNextRefTimestamp(100);
	}
}
