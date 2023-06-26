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

public enum Variant {
	NCS,
	RFC_4122,
	MICROSOFT,
	RESERVED;

	public static byte numFromVariantOctet(int octet) {
		int masked = (octet & 0b11100000) >> 5;
		return (byte) masked;
	}

	public static Variant fromVariantOctet(int num) {
		int masked = RESERVED.numFromVariantOctet(num);
		return Variant.fromInt(masked);
	}

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
