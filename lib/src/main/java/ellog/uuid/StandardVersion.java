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

/**
 * The UUID version as defined in RFC 4122 and the new UUID version draft.
 *
 * @see <a href="https://tools.ietf.org/html/rfc4122">RFC 4122</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/draft-ietf-uuidrev-rfc4122bis-14">New UUID Format Draft</a>
 */
public enum StandardVersion {
	/** Version 1: Time-based with MAC address. */
	TIME_BASED(1),
	/** Version 2: DCE Security with embedded POSIX UIDs. */
	DCE_SECURITY(2),
	/** Version 3: Name-based with MD5 hashing. */
	NAME_BASED_MD5(3),
	/** Version 4: Random UUID. */
	RANDOM(4),
	/** Version 5: Name-based with SHA-1 hashing. */
	NAME_BASED_SHA1(5),
	/** Version 6: Version 1 field-compatible, ordered time-based. */
	TIME_BASED_ORDERED_LEGACY(6),
	/** Version 7: Time-based, ordered time-based. */
	TIME_BASED_ORDERED(7),
	/** Version 8: Vendor-specific. */
	VENDOR_SPECIFIC(8);

	/** The value of this version as defined by the RFC. */
	public final int value;

	StandardVersion(int value) {
		this.value = value;
	}

	/**
	 * Get the version as enum based on the given number.
	 *
	 * @param value The version number.
	 * @throws IllegalArgumentException if the given value is not a known version number.
	 * @return The version enum value.
	 */
	public static StandardVersion fromInt(int value) {
		StandardVersion[] versions = StandardVersion.values();
		if (value <= 0 || value > versions.length) {
			throw new IllegalArgumentException("Invalid UUID version: " + value);
		} else {
			return versions[value-1];
		}
	}
}
