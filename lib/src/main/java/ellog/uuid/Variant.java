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
 * The variant of a UUID according to RFC 4122.
 */
public enum Variant {
	/** Reserved for NCS compatibility. */
	NCS,
	/** The variant specified in RFC 4122. */
	RFC_4122,
	/** Reserved for Microsoft compatibility. */
	MICROSOFT,
	/** Reserved for future definition. */
	RESERVED;

	/**
	 * Extract the variant number from the variant octet.
	 *
	 * The variant number are the 3 most significant bits of the 8th octet of the UUID.
	 *
	 * @param octet The variant octet to extract the variant number from.
	 * @return The variant number extracted from the variant octet.
	 */
	public static byte numFromVariantOctet(int octet) {
		int masked = (octet & 0b11100000) >> 5;
		return (byte) masked;
	}

	/**
	 * Extract the variant enum value from the variant octet.
	 *
	 * This function is the combination of {@link #numFromVariantOctet(int)} and {@link #fromInt(int)}.
	 *
	 * @param num The variant octet.
	 * @return The variant enum value corresponding to the given variant octet.
	 */
	public static Variant fromVariantOctet(int num) {
		int masked = RESERVED.numFromVariantOctet(num);
		return Variant.fromInt(masked);
	}

	/**
	 * Convert a variant number to a Variant enum.
	 *
	 * The variant number has to be extracted from the octet prior to being used in this function.
	 * It is expected the number is a 3 bit number, meaning only the 3 least significant bits are considered.
	 *
	 * @param variantValue The variant number to convert to the enum value.
	 * @return The variant enum value corresponding to the given number.
	 */
	public static Variant fromInt(int variantValue) {
		if ((variantValue & 0b100) == 0b000) {
			return NCS;
		} else if ((variantValue & 0b110) == 0b100) {
			return RFC_4122;
		} else {
			if ((variantValue & 0b1) == 0b0) {
				return MICROSOFT;
			} else {
				return RESERVED;
			}
		}
	}
}
