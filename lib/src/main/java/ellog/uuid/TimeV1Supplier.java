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

import java.util.function.Supplier;

/**
 * This class generates time-based UUIDs according to version 1.
 */
public class TimeV1Supplier extends TimeBasedSupplier implements Cloneable {

	@Override
	public TimeV1Supplier clone() {
		try {
			return (TimeV1Supplier) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException("Cloning of TimeV1Supplier failed.", ex);
		}
	}

	/**
	 * Create a new supplier using the default time provider.
	 * @see TimeV1Supplier#TimeV1Supplier(TimeProviderV1)
	 */
	public TimeV1Supplier() {
		this(TimeProviderV1.create());
	}

	/**
	 * Create a new supplier using the given time provider.
	 *
	 * The address is loaded from the system, falling back to a random value if it is not accessible.
	 * The clock sequence is set to a random value.
	 * @param timeProvider The time provider to use.
	 */
	public TimeV1Supplier(TimeProviderV1 timeProvider) {
		super(StandardVersion.TIME_BASED, timeProvider);
		loadHostAddress();
		builder.setRandomClockSequence();
	}

	@Override
	public StandardUUID get() {
		long ts = timeProvider.getNextRefTimestamp100ns();

		Supplier<StandardUUID> buildFun = () -> builder.setTimestampLow((int) ts)
			.setTimestampMid((short) (ts >> 32))
			.setTimestampHigh((short) (ts >> 48))
			.build();
		if (isSynchronized()) {
			synchronized (builder) {
				return buildFun.get();
			}
		} else {
			return buildFun.get();
		}
	}

	/**
	 * Set the clock sequence value to a random value.
	 *
	 * As per RFC 4122, the clock sequence value can be changed in order to avoid collisions.
	 * This might be necessary, when the precision of the time is not good enough or simply unknown.
	 * @return This supplier for method chaining.
	 */
	public TimeV1Supplier randomClockSequence() {
		builder.setRandomClockSequence();
		return this;
	}

}
