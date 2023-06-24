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

public enum StandardVersion {
	TIME_BASED(1),
	DCE_SECURITY(2),
	NAME_BASED_MD5(3),
	RANDOM(4),
	NAME_BASED_SHA1(5),
	TIME_BASED_ORDERED_LEGACY(6),
	TIME_BASED_ORDERED(7),
	VENDOR_SPECIFIC(8);

	public final int value;

	StandardVersion(int value) {
		this.value = value;
	}

	public static StandardVersion fromInt(int value) {
		StandardVersion[] versions = StandardVersion.values();
		if (value >= versions.length) {
			throw new IllegalArgumentException("Invalid UUID version: " + value);
		} else {
			return versions[value];
		}
	}
}
