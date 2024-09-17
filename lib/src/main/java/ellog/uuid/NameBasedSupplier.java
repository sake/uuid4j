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

/**
 * A supplier for UUIDs based on a name and a namespace.
 *
 * Note that this class is not thread safe.
 * If a thread safe version is needed the {@link  #get()} method must be synchronized.
 * Also make sure no one is changing the namespace and node value during the generation of the UUID.
 * For this supplier, it makes sense to use a different supplier for each thread, so the synchronization is not needed.
 *
 * @see <a href="https://tools.ietf.org/html/rfc4122#section-4.3">RFC 4122 § 4.3</a>
 */
public class NameBasedSupplier extends StandardUUIDSupplierBase implements Cloneable {

	/** The namespace UUID for DNS names according to RFC 4122 Appendix C. */
	public static final StandardUUID NS_DNS = StandardUUID.parseHex("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
	/** The namespace UUID for URLs according to RFC 4122 Appendix C. */
	public static final StandardUUID NS_URL = StandardUUID.parseHex("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
	/** The namespace UUID for OIDs according to RFC 4122 Appendix C. */
	public static final StandardUUID NS_OID = StandardUUID.parseHex("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
	/** The namespace UUID for X.500 DNs according to RFC 4122 Appendix C. */
	public static final StandardUUID NS_X500 = StandardUUID.parseHex("6ba7b814-9dad-11d1-80b4-00c04fd430c8");

	/** The message digest used to calculate the hash. */
	protected MessageDigest digest;
	/** The namespace UUID. */
	protected UUID namespace;
	/** The name data. */
	protected byte[] data;

	@Override
	public NameBasedSupplier clone() {
		try {
			NameBasedSupplier clone = (NameBasedSupplier) super.clone();
			clone.digest = (MessageDigest) digest.clone();
			if (data != null) {
				clone.data = data.clone();
			}
			return clone;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException("Cloning of NameBasedSupplier failed.", ex);
		}
	}

	/**
	 * Create a new name-based UUID supplier.
	 *
	 * @param version The UUID version to use.
	 * @param digest The message digest to use.
	 * @param namespace The namespace UUID to use.
	 */
	public NameBasedSupplier(StandardVersion version, MessageDigest digest, UUID namespace) {
		super(version);
		this.digest = digest;
		this.namespace = namespace;
	}

	/**
	 * Create a new name-based UUID supplier according to version 3.
	 *
	 * It is initialized with the given namespace and uses MD5 as digest algorithm.
	 * Note that RFC 4122 recommends to use version 5 instead of version 3.
	 *
	 * @param namespace The namespace UUID to use.
	 * @return A new name-based UUID supplier according to version 3.
	 */
	public static NameBasedSupplier version3(StandardUUID namespace) {
		try {
			return new NameBasedSupplier(StandardVersion.NAME_BASED_MD5, MessageDigest.getInstance("MD5"), namespace);
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("There is no MD5 implementation present on this system.", e);
		}
	}

	/**
	 * Create a new name-based UUID supplier according to version 5.
	 *
	 * It is initialized with the given namespace and uses SHA-1 as digest algorithm.
	 *
	 * @param namespace The namespace UUID to use.
	 * @return A new name-based UUID supplier according to version 5.
	 */
	public static NameBasedSupplier version5(UUID namespace) {
		try {
			return new NameBasedSupplier(StandardVersion.NAME_BASED_SHA1, MessageDigest.getInstance("SHA-1"), namespace);
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("There is no SHA-1 implementation present on this system.", e);
		}
	}

	/**
	 * Set the name data.
	 *
	 * @param data The name data.
	 * @return This instance.
	 */
	public NameBasedSupplier setData(byte[] data) {
		this.data = data;
		return this;
	}
	/**
	 * Set the namespace UUID.
	 *
	 * @param namespace The namespace UUID.
	 * @return This instance.
	 */
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
