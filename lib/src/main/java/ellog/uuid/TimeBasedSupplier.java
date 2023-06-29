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

/**
 * Base class for time-based UUID suppliers.
 */
public abstract class TimeBasedSupplier extends StandardUUIDSupplierBase {

	/**
	 * The time provider used to get the current time.
	 */
	protected final TimeProviderV1 timeProvider;

	/**
	 * Create a new time-based UUID supplier.
	 * @param version The version to use.
	 */
	public TimeBasedSupplier(StandardVersion version) {
		this(version, TimeProviderV1.create());
	}

	/**
	 * Create a new time-based UUID supplier.
	 * @param version The version to use.
	 * @param timeProvider The time provider to use.
	 */
	public TimeBasedSupplier(StandardVersion version, TimeProviderV1 timeProvider) {
		super(version);
		this.timeProvider = timeProvider;
	}

	/**
	 * Set the address to used in the node value.
	 * @param address The address to use.
	 * @return This supplier for method chaining.
	 */
	public TimeBasedSupplier setAddress(long address) {
		builder.setNode(address);
		return this;
	}

	/**
	 * Set the address to used in the node value.
	 * @param hwAddr The address to use.
	 * @return This supplier for method chaining.
	 * @throws IllegalArgumentException If the address is not 6 bytes long.
	 */
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

	/**
	 * Load the hardware address of the first non-loopback network interface or a random value if no interface can be used.
	 * @return This supplier for method chaining.
	 */
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

	/**
	 * Load a random address into the node value.
	 * @return This supplier for method chaining.
	 */
	public TimeBasedSupplier loadRandomAddress() {
		builder.setRandomNodeAddress();
		return this;
	}

	/**
	 * Set the clock sequence to use.
	 * @param sequence The clock sequence to use.
	 * @return This supplier for method chaining.
	 */
	protected TimeBasedSupplier setClockSequence(int sequence) {
		builder.setClockSequence(sequence);
		return this;
	}

}
