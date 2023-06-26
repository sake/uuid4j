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

public class StandardUUIDBuilder {

	private static class RandomHolder {
		static SecureRandom numberGenerator = new SecureRandom();
	}

	private byte version;
	private long timestamp;
	private short clockSequence;
	private long node;

	private SecureRandom rand;

	public StandardUUIDBuilder setRandomImpl(SecureRandom rand) {
		this.rand = rand;
		return this;
	}

	protected SecureRandom getSecRandom() {
		if (rand == null) {
			rand = RandomHolder.numberGenerator;
		}
		return rand;
	}


	public StandardUUIDBuilder setVersion(int version) {
		this.version = (byte) (version & 0x0F);
		return this;
	}

	public StandardUUIDBuilder setVersion(StandardVersion version) {
		return setVersion(version.value);
	}

	public StandardUUIDBuilder setTimestamp(long timestamp) {
		this.timestamp = timestamp & 0x0FFFFFFFFFFFFFFFL;
		return this;
	}

	public StandardUUIDBuilder setTimestampLow(int timestampLow) {
		this.timestamp = timestamp | (timestampLow << 28);
		return this;
	}
	public StandardUUIDBuilder setTimestampMid(short timestampMid) {
		this.timestamp = timestamp | (timestampMid << 12);
		return this;
	}
	public StandardUUIDBuilder setTimestampHigh(short timestampLow) {
		// 12 bits
		this.timestamp = timestamp | (timestampLow & 0x0FFF);
		return this;
	}

	public StandardUUIDBuilder setRandomTimestamp() {
		return setTimestamp(getSecRandom().nextLong());
	}

	public StandardUUIDBuilder setClockSequence(int clockSequence) {
		this.clockSequence = (short) (clockSequence & 0x3FFF);
		return this;
	}

	public StandardUUIDBuilder setRandomClockSequence() {
		return setClockSequence(getSecRandom().nextInt(0x4000));
	}

	public StandardUUIDBuilder setNode(long node) {
		this.node = node & 0x0000FFFFFFFFFFFFL;
		return this;
	}

	public StandardUUIDBuilder setRandomNode() {
		return setNode(getSecRandom().nextLong());
	}
	public StandardUUIDBuilder setRandomNodeAddress() {
		long addr = getSecRandom().nextLong();
		return setNode(addr | (1 << 40));
	}

	public StandardUUID build() {
		byte[] octets = new byte[16];
		ByteBuffer buf = ByteBuffer.wrap(octets);

		buf.putInt((int) (timestamp & 0xFFFFFFFFL));
		buf.putShort((short) ((timestamp >>> 32) & 0xFFFFL));
		buf.putShort((short) (((timestamp >>> 48) & 0x0FFF) | (version << 12)));
		buf.putShort((short) (clockSequence | (0b10 << 14)));
		buf.putShort((short) (node >>> 32));
		buf.putInt((int) (node & 0xFFFFFFFF));

		return new StandardUUID(octets);
	}

}
