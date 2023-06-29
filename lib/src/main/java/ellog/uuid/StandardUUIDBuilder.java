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

import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * This class is used to build UUIDs according to RFC 4122.
 *
 * Note that this class is not thread-safe as it mutates its state.
 */
public class StandardUUIDBuilder {

	private static class RandomHolder {
		static SecureRandom numberGenerator = new SecureRandom();
	}

	private byte version;
	private int tsLow;
	private short tsMid;
	private short tsHigh;
	private short clockSequence;
	private long node;

	private SecureRandom rand;

	/**
	 * Set the random number generator implementation.
	 * @param rand The random number generator to use.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setRandomImpl(SecureRandom rand) {
		this.rand = rand;
		return this;
	}

	/**
	 * Gets the random number generator used in this builder instance.
	 * @return The random number generator used in this instance.
	 */
	protected SecureRandom getSecRandom() {
		if (rand == null) {
			rand = RandomHolder.numberGenerator;
		}
		return rand;
	}


	/**
	 * Set the version of the UUID to build.
	 * @param version The version to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setVersion(int version) {
		this.version = (byte) (version & 0x0F);
		return this;
	}

	/**
	 * Set the version of the UUID to build.
	 * @param version The version to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setVersion(StandardVersion version) {
		return setVersion(version.value);
	}

	/**
	 * Set the timestamp of the UUID to build.
	 * @param ts The timestamp to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setTimestamp(long ts) {
		this.tsHigh = (short) (ts);
		this.tsMid = (short) (ts >>> 12);
		this.tsLow = (int) (ts >>> 28);
		return this;
	}

	/**
	 * Set the time_low value of the UUID to build.
	 * @param timestampLow The timestamp_low value to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setTimestampLow(int timestampLow) {
		this.tsLow = timestampLow;
		return this;
	}
	/**
	 * Set the time_mid value of the UUID to build.
	 * @param timestampMid The timestamp_mid value to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setTimestampMid(short timestampMid) {
		this.tsMid = timestampMid;
		return this;
	}
	/**
	 * Set the time_high value of the UUID to build.
	 * @param timestampHigh The time_high value to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setTimestampHigh(short timestampHigh) {
		this.tsHigh = timestampHigh;
		return this;
	}

	/**
	 * Set the timestamp of the UUID to build to a random value.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setRandomTimestamp() {
		return setTimestamp(getSecRandom().nextLong());
	}

	/**
	 * Set the timestamp of the UUID to build to a random value.
	 * @param clockSequence The clock sequence to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setClockSequence(int clockSequence) {
		this.clockSequence = (short) (clockSequence & 0x3FFF);
		return this;
	}

	/**
	 * Set the clock sequence of the UUID to build to a random value.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setRandomClockSequence() {
		return setClockSequence(getSecRandom().nextInt());
	}

	/**
	 * Set the value of the node field of the UUID to build.
	 * @param node The node value to set.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setNode(long node) {
		this.node = node & 0x0000FFFFFFFFFFFFL;
		return this;
	}

	/**
	 * Set the value of the node field of the UUID to build to a random value.
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setRandomNode() {
		return setNode(getSecRandom().nextLong());
	}

	/**
	 * Set the value of the node field of the UUID to build to a random address value.
	 *
	 * Random addresses are required to have the multicast bit set.
	 *
	 * @return This builder for method chaining.
	 */
	public StandardUUIDBuilder setRandomNodeAddress() {
		long addr = getSecRandom().nextLong();
		return setNode(addr | (1 << 40));
	}

	/**
	 * Build the UUID.
	 * @return The newly generated UUID.
	 */
	public StandardUUID build() {
		byte[] octets = new byte[16];
		ByteBuffer buf = ByteBuffer.wrap(octets);

		buf.putInt(tsLow);
		buf.putShort(tsMid);
		// only 12 bits used
		buf.putShort((short) ((tsHigh & 0x0FFF) | (version << 12)));
		buf.putShort((short) (clockSequence | (0b10 << 14)));
		buf.putShort((short) (node >>> 32));
		buf.putInt((int) (node & 0xFFFFFFFF));

		return new StandardUUID(octets);
	}

}
