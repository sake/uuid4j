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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Collections;

public abstract class TimeBasedSupplier extends StandardUUIDSupplierBase {
	protected TimeProvider timeProvider = TimeProvider.GLOBAL;

	public TimeBasedSupplier(StandardVersion version) {
		super(version);
	}

	public TimeBasedSupplier setAddress(long address) {
		builder.setNode(address);
		return this;
	}
	public TimeBasedSupplier setAddress(byte[] hwAddr) {
		if (hwAddr.length != 6) {
			throw new IllegalArgumentException("Hardware address must be 6 bytes long");
		}

		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.put((byte) 0);
		buf.put((byte) 0);
		buf.put(hwAddr);
		long addr = buf.getLong(0);
		return setAddress(addr);
	}

	public TimeBasedSupplier loadHostAddress() {
		try {
			for (NetworkInterface next : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				// TODO: find out if we should reject virtual or sub-interfaces
				if (!next.isLoopback() && next.isUp()) {
					byte[] hwAddr = next.getHardwareAddress();
					if (hwAddr != null) {
						return setAddress(hwAddr);
					}
				}
			}
		} catch (SocketException e) {
			// ignore and use the fallback
		}

		// nothing found fall back to random
		return this.loadRandomAddress();
	}
	public TimeBasedSupplier loadRandomAddress() {
		builder.setRandomNodeAddress();
		return this;
	}

	protected TimeBasedSupplier setClockSequence(int sequence) {
		builder.setClockSequence(sequence);
		return this;
	}

}
