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

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UUIDv7Test {

	@Test
	void testOrdered() {
		TimeV7Supplier supplier = new TimeV7Supplier();
		StandardUUID uuid1 = supplier.get();
		StandardUUID uuid2 = supplier.get();

		// check that the compareTo is working properly
		assertTrue(uuid1.compareTo(uuid2) < 0);
		assertTrue(uuid2.compareTo(uuid1) > 0);
	}

	@Test
	void testOrderedBatch() {
		TimeV7Supplier supplier = new TimeV7Supplier();
		Optional<StandardUUID> lastUUID = supplier.toStream()
			.limit(100000)
			.reduce((uuid1, uuid2) -> {
				assertTrue(uuid1.compareTo(uuid2) < 0);
				return uuid2;
			});
		assertTrue(lastUUID.isPresent());
	}
}
