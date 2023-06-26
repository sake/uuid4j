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

public class StandardUUID extends UUID {

	public StandardUUID(byte[] octets) {
		super(octets, Variant.RFC_4122);
	}

	public static StandardUUID parseHex(String uuid) throws ClassCastException {
		UUID parsedUuid = UUID.parseHex(uuid);
		return StandardUUID.class.cast(parsedUuid);
	}

	protected int timeLow() {
		return dataBuf.getInt(0);
	}

	protected short timeMid() {
		return dataBuf.getShort(4);
	}

	protected short timeHiAndVersion() {
		return dataBuf.getShort(6);
	}

	protected byte clockSeqHiAndReserved() {
		return dataBuf.get(8);
	}

	protected byte clockSeqLow() {
		return dataBuf.get(9);
	}

	public byte[] nodeBytes() {
		byte[] result = new byte[6];
		dataBuf.get(result, 10, 6);
		return result;
	}

	public byte versionRaw() {
		return (byte) ((timeHiAndVersion() & 0xF000) >>> 12);
	}

	public StandardVersion version() {
		return StandardVersion.fromInt(versionRaw());
	}

	public long timestamp() {
		long ts
			= (timeHiAndVersion() & 0x0FFF) << (60-12)
			| (timeMid() & 0xFFFFL) << (60-12-16)
			| timeLow();
		return ts;
	}

	public short clockSequence() {
		return (short) ((clockSeqHiAndReserved() & 0x3F) << 8 | clockSeqLow());
	}

	public long node() {
		long data = dataBuf.getLong(8);
		// remove the two bytes which are too much (node is 48 bits)
		return data & 0x0000FFFFFFFFFFFFL;
	}


	public static StandardUUID createRandom() {
		return new Version4Supplier().get();
	}

	public static StandardUUID createNameBased(StandardUUID namespace, byte[] name) {
		return NameBasedSupplier.version5(namespace)
			.setData(name)
			.get();
	}

	public static StandardUUID createTimeV1() {
		return new TimeV1Supplier().get();
	}

	public static StandardUUID createTimeV6() {
		return new TimeV6Supplier().get();
	}

	public static StandardUUID createTimeV7() {
		return new TimeV7Supplier().get();
	}

}
