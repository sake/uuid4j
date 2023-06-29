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

import java.io.Serializable;

public class StandardUUID extends UUID implements Serializable {

	private static final long serialVersionUID = 1L;

	protected StandardUUID(byte[] octets) {
		super(octets, Variant.RFC_4122);
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
	 * @throws ClassCastException If the variant is not RFC 4122.
	 * @return The parsed UUID.
	 */
	public static StandardUUID parseHex(String uuid) throws ClassCastException {
		UUID parsedUuid = UUID.parseHex(uuid);
		return StandardUUID.class.cast(parsedUuid);
	}

	/**
	 * Load the UUID from the given byte array.
	 * @param bytes The octets of the  UUID.
	 * @return The UUID object.
	 * @throws IllegalArgumentException If the octets are not 16 octets long.
	 * @throws IndexOutOfBoundsException If the variant octet is not reachable.
	 * @throws ClassCastException If the variant is not RFC 4122.
	 */
	public static UUID fromBytes(byte[] bytes) {
		UUID parsedUuid = UUID.buildVariantObject(bytes);
		return StandardUUID.class.cast(parsedUuid);
	}

	protected int timeLow() {
		return dataBuf().getInt(0);
	}

	protected short timeMid() {
		return dataBuf().getShort(4);
	}

	protected short timeHiAndVersion() {
		return dataBuf().getShort(6);
	}

	protected byte clockSeqHiAndReserved() {
		return dataBuf().get(8);
	}

	protected byte clockSeqLow() {
		return dataBuf().get(9);
	}

	public byte[] nodeBytes() {
		byte[] result = new byte[6];
		dataBuf().get(result, 10, 6);
		return result;
	}

	public byte versionRaw() {
		return (byte) ((timeHiAndVersion() & 0xF000) >>> 12);
	}

	public StandardVersion version() {
		return StandardVersion.fromInt(versionRaw());
	}

	public long timestamp() {
		if (version() == StandardVersion.TIME_BASED) {
			long ts
				= (long) (timeHiAndVersion() & 0x0FFF) << (60-12)
				| (timeMid() & 0xFFFFL) << (60-12-16)
				| timeLow();
			return ts;
		} else {
			long ts
				= ((long) timeLow() << (16 + 12))
				| (timeMid() << 12)
				| (timeHiAndVersion() & 0x0FFF);
			return ts;
		}
	}

	public short clockSequence() {
		return (short) ((clockSeqHiAndReserved() & 0x3F) << 8 | clockSeqLow());
	}

	public long node() {
		long data = dataBuf().getLong(8);
		// remove the two bytes which are too much (node is 48 bits)
		return data & 0x0000FFFFFFFFFFFFL;
	}


	public static StandardUUID createRandom() {
		return new Version4Supplier().get();
	}

	public static StandardUUID createNameBased(UUID namespace, byte[] name) {
		return NameBasedSupplier.version5(namespace)
			.setData(name)
			.get();
	}

	private static class TimeV1Holder {
		static final TimeV1Supplier INSTANCE = new TimeV1Supplier();
	}
	public static StandardUUID createTimeV1() {
		return TimeV1Holder.INSTANCE.get();
	}

	private static class TimeV7Holder {
		static final TimeV7Supplier INSTANCE = new TimeV7Supplier();
	}
	public static StandardUUID createTimeV7() {
		return TimeV7Holder.INSTANCE.get();
	}

}
