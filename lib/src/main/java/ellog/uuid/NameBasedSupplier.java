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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NameBasedSupplier extends StandardUUIDSupplierBase {

	public static final StandardUUID NS_DNS = StandardUUID.parseHex("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
	public static final StandardUUID NS_URL = StandardUUID.parseHex("");
	public static final StandardUUID NS_OID = StandardUUID.parseHex("");
	public static final StandardUUID NS_X500 = StandardUUID.parseHex("");

	protected final MessageDigest digest;
	protected StandardUUID namespace;
	protected byte[] data;

	public NameBasedSupplier(StandardVersion version, MessageDigest digest, StandardUUID namespace) {
		super(version);
		this.digest = digest;
		this.namespace = namespace;
	}

	public static NameBasedSupplier version3(StandardUUID namespace) {
		try {
			return new NameBasedSupplier(StandardVersion.NAME_BASED_MD5, MessageDigest.getInstance("MD5"), namespace);
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("There is no MD5 implementation present on this system.", e);
		}
	}
	public static NameBasedSupplier version5(StandardUUID namespace) {
		try {
			return new NameBasedSupplier(StandardVersion.NAME_BASED_SHA1, MessageDigest.getInstance("SHA-1"), namespace);
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("There is no SHA-1 implementation present on this system.", e);
		}
	}

	public NameBasedSupplier setData(byte[] data) {
		this.data = data;
		return this;
	}
	public NameBasedSupplier setNamespace(StandardUUID namespace) {
		this.namespace = namespace;
		return this;
	}

	@Override
	public StandardUUID get() {
		byte[] nsBytes = namespace.getBytes();
		digest.update(nsBytes);
		digest.update(data);
		byte[] hash = digest.digest();
		ByteBuffer hashView = ByteBuffer.wrap(hash);

		return builder.setTimestampLow(hashView.getInt(0))
			.setTimestampMid(hashView.getShort(4))
			.setTimestampHigh(hashView.getShort(6))
			.setClockSequence(hashView.getShort(8))
			// leading bits are cut by the builder, so extract long including earlier positions
			.setNode(hashView.getLong(8))
			.build();
	}
}
