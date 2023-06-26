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

	protected TimeProviderV7 timeProvider;

	public TimeV7Supplier(TimeProviderV7 timeProvider) {
		super(StandardVersion.TIME_BASED_ORDERED);
		this.timeProvider = timeProvider;
	}
	public TimeV7Supplier() {
		this(new TimeProviderV7());
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
		short tsHigh = counterOrRand(numCounterBits, counter, 12);
		short cs = counterOrRand(numCounterBits - 12, counter >>> 12, 14);

		return builder.setTimestampLow((int) (ts >>> 16))
			.setTimestampMid((short) ts)
			.setTimestampHigh(tsHigh)
			.setClockSequence(cs)
			.setRandomNode()
			.build();
	}

}
