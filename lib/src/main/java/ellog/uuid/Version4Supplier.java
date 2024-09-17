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

import java.security.SecureRandom;
import java.util.function.Supplier;

/**
 * This class generates random UUIDs according to version 4.
 */
public class Version4Supplier extends StandardUUIDSupplierBase implements Cloneable {

	@Override
	public Version4Supplier clone() {
		try {
			return (Version4Supplier) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException("Cloning of Version4Supplier failed.", ex);
		}
	}

	/**
	 * Create a new supplier for version 4 UUIDs.
	 *
	 * This constructor allows to customize the random number generator.
	 *
	 * @param random The random number generator to use.
	 */
	public Version4Supplier(SecureRandom random) {
		this();
		builder.setRandomImpl(random);
	}

	/**
	 * Create a new supplier for version 4 UUIDs.
	 *
	 * This constructor uses the default random number generator.
	 */
	public Version4Supplier() {
		super(StandardVersion.RANDOM);
	}

	@Override
	public StandardUUID get() {
		Supplier<StandardUUID> buildFun = () -> builder.setRandomTimestamp()
			.setRandomClockSequence()
			.setRandomNode()
			.build();
		if (isSynchronized()) {
			synchronized (builder) {
				return buildFun.get();
			}
		} else {
			return buildFun.get();
		}
	}
}
