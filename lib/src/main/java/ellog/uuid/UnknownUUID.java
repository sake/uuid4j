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

/**
 * A UUID of an unknown or unimplemented variant.
 *
 * Instances of this class do not provide any other accessors than the variant as the internal structure is not understood further.
 */
public class UnknownUUID extends UUID implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new UUID from the given octets.
	 *
	 * This constructor can be used for every variant as there is no further processing of the internal structure beyond reading the variant field.
	 *
	 * @param octets The octets to use for this UUID.
	 * @throws IllegalArgumentException If the octets are not 16 octets long.
	 */
	protected UnknownUUID(byte[] octets) {
		super(octets);
	}

}
