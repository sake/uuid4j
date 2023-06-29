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

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract UUID class.
 *
 * This class encapsulates the 16 octets of the UUID and provides methods to access them.
 * It also provides a method to parse and emit the standard UUID string representation.
 *
 * <p>UUIDs are immutable objects, which means they cannot be changed after
 * creation.  All methods that modify a UUID return a new instance with the
 * modified data.
 *
 * <p>UUIDs are comparable to each other and are ordered by comparing their 16 octets.</p>
 *
 * <p>UUIDs can be parsed and serialized in their standard string representation:</p>
 *
 * <pre>
 *   01234567-89ab-cdef-0123-456789abcdef
 * </pre>
 *
 * <p>UUIDs can also be formatted to strings in the URN and OID representation:</p>
 *
 * <pre>
 *   urn:uuid:01234567-89ab-cdef-0123-456789abcdef
 * </pre>
 * <pre>
 *   urn:oid:01234567890123456789
 * </pre>
 *
 * <p>UUIDs can also be formatted to decimal representation:</p>
 * <pre>
 *   01234567890123456789
 * </pre>
 *
 */
public abstract class UUID implements Comparable<UUID>, Serializable {

	private static final long serialVersionUID = 1L;

	/** The standard UUID string representation pattern. */
	public static final Pattern HEX_PATTERN = Pattern.compile(
		"^([0-9a-f]{8})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{12})$",
		Pattern.CASE_INSENSITIVE
	);

	/** The NIL UUID consisting only of zeroes. */
	public static UUID NIL_UUID = new UnknownUUID(new byte[16]);
	/** The MAX UUID consisting only of ones. */
	public static UUID MAX_UUID;

	static {
		byte[] maxBytes = new byte[16];
		Arrays.fill(maxBytes, (byte) 0xFF);
		MAX_UUID = new UnknownUUID(maxBytes);
	}

	/**
	 * Private buffer object.
	 *
	 * Can't be final because of serialization, so it's better to have it private.
 	 */
	private transient ByteBuffer dataBuffer;
	/**
	 * The buffer containing the UUID octets.
	 * @return The buffer containing the UUID octets.
	 */
	protected final ByteBuffer dataBuf() {
		return dataBuffer;
	}

	/**
	 * Construct a new UUID from the given octets.
	 *
	 * @param octets The octets of the UUID. Must be 16 octets long.
	 * @throws IllegalArgumentException If the octets are not 16 octets long.
	 */
	protected UUID(byte[] octets) {
		if (octets.length != 16) {
			throw new IllegalArgumentException("UUIDs must be 16 octets long");
		}
		this.dataBuffer = ByteBuffer.allocate(16);
		this.dataBuffer.put(octets);
	}

	/**
	 * Construct a new UUID from the given octets.
	 *
	 * This constructor also asserts that the resulting UUID is of the named variant.
	 *
	 * @param octets The octets of the UUID. Must be 16 octets long.
	 * @param expectedVariant The variant the UUID must be of.
	 * @throws IllegalArgumentException If the octets are not 16 octets long or the UUID is of a different variant than expected.
x	 */
	protected UUID(byte[] octets, Variant expectedVariant) {
		this(octets);
		if (variant() != expectedVariant) {
			throw new IllegalArgumentException("UUIDs must be of variant " + expectedVariant);
		}
	}

	/**
	 * Gets a copy of the octets that make up the UUID.
	 *
	 * @return The octets that make up the UUID.
	 */
	public byte[] getBytes() {
		return dataBuf().array();
	}

	/**
	 * Parse the standard UUID string representation.
	 *
	 * The string must satisfy the pattern defined in RFC 4122.
	 * <pre>
	 *     01234567-89ab-cdef-0123-456789abcdef
	 * </pre>
	 *
	 * @param uuid The UUID string representation.
	 * @throws IllegalArgumentException If the string does not satisfy the pattern.
	 * @return The parsed UUID.
	 */
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

	/**
	 * Load the UUID from the given byte array.
	 * @param bytes The octets of the  UUID.
	 * @return The UUID object.
	 * @throws IllegalArgumentException If the octets are not 16 octets long.
	 * @throws IndexOutOfBoundsException If the variant octet is not reachable.
	 */
	public static UUID fromBytes(byte[] bytes) {
		return UUID.buildVariantObject(bytes);
	}

	/**
	 * Build the object matching the variant of the UUID.
	 *
	 * This methods first looks at the variant octet of the UUID to determine the variant.
	 * Afterwards it selects the appropriate class to build.
	 *
	 * @param uuidData The UUID octets.
	 * @return The UUID object.
	 * @see UnknownUUID
	 * @see StandardUUID
	 */
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

	/**
	 * Get the variant value of the UUID.
	 * @return The variant value of the UUID.
	 */
	public byte variantRaw() {
		return Variant.numFromVariantOctet(dataBuf().get(8));
	}

	/**
	 * Get the variant of the UUID.
	 * @return The variant of the UUID.
	 */
	public Variant variant() {
		return Variant.fromVariantOctet(dataBuf().get(8));
	}

	/**
	 * Prints the standard representation of the UUID.
	 *
	 * The standard representation is the canonical representation of the UUID as defined in RFC 4122.
	 *  <pre>
	 *    01234567-89ab-cdef-0123-456789abcdef
	 *  </pre>
	 *
	 * @return The standard representation of the UUID.
	 */
	@Override
	public String toString() {
		return String.format("%08x-%04x-%04x-%04x-%04x%08x",
			dataBuf().getInt(0),
			dataBuf().getShort(4),
			dataBuf().getShort(6),
			dataBuf().getShort(8),
			dataBuf().getShort(10),
			dataBuf().getInt(12)
		);
	}

	/**
	 * Prints the decimal representation of the UUID.
	 *
	 * <p>The decimal representation is the UUID octets printed as a decimal number.</p>
	 * <p>This implementation uses {@link BigInteger#toString(int)} with radix 10 to create the string representation.</p>
	 *
	 * @return The decimal representation of the UUID.
	 */
	public String toStringDecimal() {
		BigInteger bigInt = new BigInteger(dataBuf().array());
		return bigInt.toString(10);
	}

	/**
	 * Prints the binary representation of the UUID.
	 *
	 * <p>The binary representation is the UUID octets printed as a binary number.</p>
	 * <p>This implementation uses {@link Long#toBinaryString(long)} to create the string representation.</p>
	 * @return The binary representation of the UUID.
	 */
	public String toStringBinary() {
		return String.format("%64s", Long.toBinaryString(dataBuf().getLong(0))).replace(' ', '0')
			+ String.format("%64s", Long.toBinaryString(dataBuf().getLong(8))).replace(' ', '0');
	}

	/**
	 * Prints the URN representation of the UUID.
	 *
	 * The URN representation is identical to the standard representation with the additional prefix {@code urn:uuid}.
	 *
	 * @return The URN representation of the UUID.
	 */
	public URI toUrn() {
		return URI.create("urn:uuid:" + this);
	}

	/**
	 * Prints the OID URN representation of the UUID.
	 *
	 * The OID URN representation is identical to the decimal representation with the additional prefix {@code urn:oid}.
	 *
	 * @return The URN OID representation of the UUID.
	 */
	public URI toOidUrn() {
		return URI.create("urn:oid:" + toStringDecimal());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UUID) {
			UUID other = (UUID) obj;
			return Arrays.equals(dataBuf().array(), other.dataBuf().array());
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(UUID o) {
		long h1 = this.dataBuf().getLong(0);
		long h2 = o.dataBuf().getLong(0);
		int c1 = Long.compareUnsigned(h1, h2);
		if (c1 != 0) {
			return c1;
		} else {
			long l1 = this.dataBuf().getLong(8);
			long l2 = o.dataBuf().getLong(8);
			int c2 = Long.compareUnsigned(l1, l2);
			return c2;
		}
	}


	public static UUID createRandom() {
		return StandardUUID.createRandom();
	}

	public static UUID createNameBased(UUID namespace, byte[] name) {
		return StandardUUID.createNameBased(namespace, name);
	}

	public static UUID createTimeV1() {
		return StandardUUID.createTimeV1();
	}

	public static UUID createTimeV7() {
		return StandardUUID.createTimeV7();
	}


	/**
	 * Method for custom serialization.
	 *
	 * @param out The object output stream.
	 * @throws IOException If an I/O error occurs.
	 * @see Serializable
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(dataBuf().array());
	}

	/**
	 * Method for custom deserialization.
	 *
	 * @param in The object input stream.
	 * @throws IOException If an I/O error occurs.
	 * @throws ClassNotFoundException If the class of the serialized object cannot be found.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		byte[] octets = (byte[]) in.readObject();
		if (octets.length != 16) {
			throw new IllegalArgumentException("UUIDs must be 16 octets long");
		}
		this.dataBuffer = ByteBuffer.wrap(octets);
	}

}
