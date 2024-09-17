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

/**
 * This class generates ordered time-based UUIDs according to version 7.
 */
public class TimeV7Supplier extends StandardUUIDSupplierBase implements Cloneable {

	/**
	 * The provider of the current time and collision counter value.
	 */
	protected final TimeProviderV7 timeProvider;
	/**
	 * The length of the collision counter in bits in the final UUID.
	 */
	protected final int fixedCounterLength;

	@Override
	public TimeV7Supplier clone() {
		try {
			return (TimeV7Supplier) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException("Cloning of TimeV7Supplier failed.", ex);
		}
	}

	/**
	 * Create a new time-based UUID supplier.
	 *
	 * The counter length is recommended to be between 12 and 42 bits.
	 * This implementation can process values between 0 and 26 as only the high timestamp and clock sequence values are used for the counter.
	 *
	 * @param timeProvider The provider of the current time and collision counter value.
	 * @param counterLength The length of the collision counter in bits in the final UUID.
	 */
	public TimeV7Supplier(TimeProviderV7 timeProvider, int counterLength) {
		super(StandardVersion.TIME_BASED_ORDERED);
		this.timeProvider = timeProvider;
		this.fixedCounterLength = counterLength;
	}

	/**
	 * Create a new time-based UUID supplier with a default counter length of 12 bits.
	 */
	public TimeV7Supplier() {
		this(TimeProviderV7.create(), 12);
	}

	private short counterOrRand(int numCounterBits, int counter, int numWidth) {
		int result = 0;
		// make sure we have no negative numbers
		numCounterBits = Math.max(0, numCounterBits);

		if (numCounterBits <= numWidth) {
			if (numCounterBits < numWidth) {
				result = (short) (builder.getSecRandom().nextInt());
				int randMask = (1 << (numWidth-numCounterBits)) - 1;
				result = result & randMask;
			}
			// set counter to leading bits
			result = (short) (result | (counter << (numWidth - numCounterBits)));
		} else {
			result = counter;
		}

		return (short) result;
	}

	@Override
	public StandardUUID get() {
		TimeProviderV7.TimeAndCounter tc = timeProvider.getNext();
		long ts = tc.time;

		int counter = tc.counter;
		int numCounterBits = tc.numCounterBits();
		if (numCounterBits > fixedCounterLength) {
			// TODO: handle counter overflow properly
			System.err.println("UUID counter overflow. Consider increasing the counter length to a value bigger than " + numCounterBits + ".");
		}
		short tsHigh = counterOrRand(fixedCounterLength, counter, 12);
		short cs = counterOrRand(fixedCounterLength - 12, counter >>> 12, 14);

		return builder.setTimestampLow((int) (ts >>> 16))
			.setTimestampMid((short) ts)
			.setTimestampHigh(tsHigh)
			.setClockSequence(cs)
			.setRandomNode()
			.build();
	}

}
