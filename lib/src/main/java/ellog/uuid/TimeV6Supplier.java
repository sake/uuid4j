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
 * This class generates ordered time-based UUIDs according to version 6.
 */
public class TimeV6Supplier extends TimeBasedSupplier {

	/**
	 * Create a new supplier with a random address and clock sequence.
	 */
	public TimeV6Supplier() {
		super(StandardVersion.TIME_BASED_ORDERED_LEGACY);
		loadRandomAddress();
		builder.setRandomClockSequence();
	}

	@Override
	public StandardUUID get() {
		long ts = timeProvider.getNextRefTimestamp100ns();
		return builder.setTimestamp(ts)
			.build();
	}

}
