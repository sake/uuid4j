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


public class TimeV7Supplier extends StandardUUIDSupplierBase {

	protected final TimeProviderV7 timeProvider;
	protected final int fixedCounterLength;

	public TimeV7Supplier(TimeProviderV7 timeProvider, int counterLength) {
		super(StandardVersion.TIME_BASED_ORDERED);
		this.timeProvider = timeProvider;
		this.fixedCounterLength = counterLength;
	}
	public TimeV7Supplier() {
		this(new TimeProviderV7(), 12);
	}

	private short counterOrRand(int numCounterBits, int counter, int numWidth) {
		int result = 0;
		// make sure we have no negative numbers
		numCounterBits = Math.max(0, numCounterBits);

		if (numCounterBits <= numWidth) {
			if (numCounterBits < numWidth) {
				result = (short) (builder.getSecRandom().nextInt());
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
