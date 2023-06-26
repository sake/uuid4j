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

import java.math.BigInteger;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UUID implements Comparable<UUID> {

	public static final Pattern HEX_PATTERN = Pattern.compile(
		"^([0-9a-f]{8})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{12})$",
		Pattern.CASE_INSENSITIVE
	);

	public static UUID NIL_UUID = new UnknownUUID(new byte[16]);
	public static UUID MAX_UUID;

	static {
		byte[] maxBytes = new byte[16];
		Arrays.fill(maxBytes, (byte) 0xFF);
		MAX_UUID = new UnknownUUID(maxBytes);
	}

	protected final ByteBuffer dataBuf;

	protected UUID(byte[] octets) {
		if (octets.length != 16) {
			throw new IllegalArgumentException("UUIDs must be 16 octets long");
		}
		this.dataBuf = ByteBuffer.allocate(16);
		this.dataBuf.put(octets);
	}

	protected UUID(byte[] octets, Variant expectedVariant) {
		this(octets);
		if (variant() != expectedVariant) {
			throw new IllegalArgumentException("UUIDs must be of variant " + expectedVariant);
		}
	}

	public byte[] getBytes() {
		return dataBuf.array();
	}

	public static UUID parseHex(String uuid) {
		Matcher match = HEX_PATTERN.matcher(uuid);
		if (match.matches()) {
			ByteBuffer b = ByteBuffer.allocate(16);

			int p1 = Integer.parseUnsignedInt(match.group(1), 16);
			b.putInt(p1);
			short p2 = (short) Integer.parseUnsignedInt(match.group(2), 16);
			b.putShort(p2);
			short p3 = (short) Integer.parseUnsignedInt(match.group(3), 16);
			b.putShort(p3);
			short p4 = (short) Integer.parseUnsignedInt(match.group(4), 16);
			b.putShort(p4);
			long p5 = Long.parseUnsignedLong(match.group(5), 16);
			b.putShort((short) ((p5 >> 32) & 0xFFFF));
			b.putInt((int) (p5 & 0xFFFFFFFF));

			byte[] bytes = b.array();
			return UUID.buildVariantObject(bytes);
		} else {
			throw new IllegalArgumentException("Provided input does not satisfy the UUID hex format.");
		}
	}

	protected static UUID buildVariantObject(byte[] uuidData) {
		switch (Variant.fromVariantOctet(uuidData[8])) {
			case RFC_4122:
				return new StandardUUID(uuidData);
			case MICROSOFT:
			case RESERVED:
			case NCS:
			default:
				return new UnknownUUID(uuidData);
		}
	}

	public byte variantRaw() {
		return Variant.numFromVariantOctet(dataBuf.get(8));
	}

	public Variant variant() {
		return Variant.fromVariantOctet(dataBuf.get(8));
	}

	@Override
	public String toString() {
		return String.format("%08x-%04x-%04x-%04x-%04x%08x",
			dataBuf.getInt(0),
			dataBuf.getShort(4),
			dataBuf.getShort(6),
			dataBuf.getShort(8),
			dataBuf.getShort(10),
			dataBuf.getInt(12)
		);
	}

	public String toStringDecimal() {
		BigInteger bigInt = new BigInteger(dataBuf.array());
		return bigInt.toString(10);
	}

	public String toStringBinary() {
		return String.format("%64s", Long.toBinaryString(dataBuf.getLong(0))).replace(' ', '0')
			+ String.format("%64s", Long.toBinaryString(dataBuf.getLong(8))).replace(' ', '0');
	}

	public URI toUrn() {
		return URI.create("urn:uuid:" + this);
	}

	public URI toOidUrn() {
		return URI.create("urn:oid:" + toStringDecimal());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UUID) {
			UUID other = (UUID) obj;
			return Arrays.equals(dataBuf.array(), other.dataBuf.array());
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(UUID o) {
		long h1 = this.dataBuf.getLong(0);
		long h2 = o.dataBuf.getLong(0);
		int c1 = Long.compareUnsigned(h1, h2);
		if (c1 != 0) {
			return c1;
		} else {
			long l1 = this.dataBuf.getLong(8);
			long l2 = o.dataBuf.getLong(8);
			int c2 = Long.compareUnsigned(l1, l2);
			return c2;
		}
	}

}
