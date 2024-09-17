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

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Base class with common functionality for StandardUUID suppliers.
 */
public abstract class StandardUUIDSupplierBase implements Supplier<StandardUUID> {

	/**
	 * The builder used to create the UUIDs.
	 */
	protected StandardUUIDBuilder builder;

	/**
	 * Flag to indicate if the supplier is synchronized.
	 * Setting this to false means that the supplier function is not thread safe, so other measures need to be taken, such als cloning the supplier.
	 */
	protected boolean isSynchronized = true;

	protected StandardUUIDSupplierBase clone() throws CloneNotSupportedException {
		StandardUUIDSupplierBase clone = (StandardUUIDSupplierBase) super.clone();
		clone.builder = builder.clone();
		return clone;
	}

	/**
	 * Create a new supplier for standard UUIDs with the given builder.
	 * @param builder The builder to use.
	 */
	protected StandardUUIDSupplierBase(StandardUUIDBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Create a new supplier for standard UUIDs with the given version.
	 * @param version The version to use.
	 */
	protected StandardUUIDSupplierBase(StandardVersion version) {
		this(new StandardUUIDBuilder());
		builder.setVersion(version);
	}

	/**
	 * Check if this supplier is thread safe.
	 * @return True if the supplier is thread safe, false otherwise.
	 */
	public boolean isSynchronized() {
		return isSynchronized;
	}

	/**
	 * Set if this supplier is thread safe.
	 * @param isSynchronized True if the supplier is thread safe, false otherwise.
	 */
	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	/**
	 * Create an infinite stream with the given supplier.
	 *
	 * Note that the stream is not parallelizable as the UUIDs are generated with only one builder which is not thread safe.
	 *
	 * @return An infinite stream of UUIDs.
	 */
	public Stream<StandardUUID> toStream() {
		return Stream.generate(this);
	}

}
