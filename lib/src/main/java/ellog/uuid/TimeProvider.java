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

public class TimeProvider {

	public static final Instant REFERENCE = Instant.parse("1582-10-15T00:00:00Z");
	public static final TimeProvider GLOBAL = new TimeProvider();

	static {
		GLOBAL.withLock = true;
	}

	protected Instant lastInstant = Instant.MIN;
	protected Clock clock = Clock.systemUTC();
	protected boolean withLock = false;


	public TimeProvider setClock(Clock clock) {
		this.clock = clock;
		return this;
	}

	public Instant getNext() {
		if (withLock) {
			return getNextLockedInternal();
		} else {
			return getNextInternal();
		}
	}

	private synchronized Instant getNextLockedInternal() {
		return getNextInternal();
	}
	private Instant getNextInternal() {
		Instant nextInstant = clock.instant();
		if (nextInstant.isAfter(lastInstant)) {
			lastInstant = nextInstant;
			return nextInstant;
		} else {
			lastInstant = lastInstant.plusNanos(1);
			return lastInstant;
		}
	}

	public long getNextTimestamp(long nanoPrecision) {
		Instant next = getNext();
		Duration interval = Duration.between(REFERENCE, next);
		long seconds = interval.getSeconds() * (1_000_000_000 / nanoPrecision);
		long nanos = interval.getNano() / nanoPrecision;
		long result = seconds + nanos;
		return result;
	}

	public long getNextTimestamp100ns() {
		return getNextTimestamp(100);
	}
}
