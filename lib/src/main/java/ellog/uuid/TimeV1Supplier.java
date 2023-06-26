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

public class TimeV1Supplier extends TimeBasedSupplier {

	public TimeV1Supplier() {
		super(StandardVersion.TIME_BASED);
		loadHostAddress();
		builder.setRandomClockSequence();
	}

	@Override
	public StandardUUID get() {
		long ts = timeProvider.getNextTimestamp100ns();
		return builder.setTimestampLow((int) ts)
			.setTimestampMid((short) (ts >> 32))
			.setTimestampHigh((short) (ts >> 48))
			.build();
	}

}
