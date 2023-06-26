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
	private int tsLow;
	private short tsMid;
	private short tsHigh;
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

	public StandardUUIDBuilder setTimestamp(long ts) {
		this.tsHigh = (short) (ts);
		this.tsMid = (short) (ts >>> 12);
		this.tsLow = (int) (ts >>> 28);
		return this;
	}

	public StandardUUIDBuilder setTimestampLow(int timestampLow) {
		this.tsLow = timestampLow;
		return this;
	}
	public StandardUUIDBuilder setTimestampMid(short timestampMid) {
		this.tsMid = timestampMid;
		return this;
	}
	public StandardUUIDBuilder setTimestampHigh(short timestampHigh) {
		this.tsHigh = timestampHigh;
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
		return setClockSequence(getSecRandom().nextInt());
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
