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

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class UUIDv1Test {

	@Test
	void genMocked() {
		TimeV1Supplier v1Supplier = new TimeV1Supplier(new TimeProviderV1() {
			@Override
			public long getNextRefTimestamp100ns() {
				return 0xF123456789ABCDEFL;
			}
		});
		v1Supplier.setClockSequence(0xF234);
		v1Supplier.setAddress(0xFFFFCBA987654321L);

		StandardUUID uuid = v1Supplier.get();

		assertEquals("89ABCDEF-4567-1123-B234-CBA987654321".toLowerCase(), uuid.toString());
	}

	@Test
	void genRealTime() {
		TimeV1Supplier v1Supplier = new TimeV1Supplier();
		v1Supplier.setClockSequence(0xF234);
		v1Supplier.setAddress(0xFFFFCBA987654321L);
		StandardUUID uuid = v1Supplier.get();

		assertEquals(StandardVersion.TIME_BASED, uuid.version());
		assertEquals(Variant.RFC_4122, uuid.variant());
		assertEquals(0x3234, uuid.clockSequence());
		assertEquals(0xCBA987654321L, uuid.node());
		// build before and after timestamp and make sure UUID TS is between the two
		Instant before = Instant.now().minusSeconds(60);
		Instant after = Instant.now().plusSeconds(1);
		Instant tsInst = reconstructTimestamp(uuid);
		assertTrue(tsInst.isAfter(before));
		assertTrue(tsInst.isBefore(after));
	}

	@Test
	void checkRandomValues() {
		TimeV1Supplier v1Supplier = new TimeV1Supplier();
		StandardUUID uuid1 = v1Supplier.get();
		v1Supplier.loadRandomAddress();
		v1Supplier.randomClockSequence();
		StandardUUID uuid2 = v1Supplier.get();

		assertNotEquals(uuid1, uuid2);
		assertNotEquals(uuid1.timestamp(), uuid2.timestamp());
		assertNotEquals(uuid1.clockSequence(), uuid2.clockSequence());
		assertNotEquals(uuid1.node(), uuid2.node());
		assertEquals(uuid1.version(), uuid2.version());
		assertEquals(uuid1.variant(), uuid2.variant());
	}

	@Test
	void checkIncreasedTime() {
		TimeV1Supplier v1Supplier = new TimeV1Supplier();
		StandardUUID uuid1 = v1Supplier.get();
		StandardUUID uuid2 = v1Supplier.get();

		assertNotEquals(uuid1, uuid2);
		assertNotEquals(uuid1.timestamp(), uuid2.timestamp());
		assertEquals(uuid1.clockSequence(), uuid2.clockSequence());
		assertEquals(uuid1.node(), uuid2.node());
		assertEquals(uuid1.version(), uuid2.version());
		assertEquals(uuid1.variant(), uuid2.variant());

		Instant t1 = reconstructTimestamp(uuid1);
		Instant t2 = reconstructTimestamp(uuid2);
		assertTrue(t2.isAfter(t1));
	}

	private Instant reconstructTimestamp(StandardUUID uuid) {
		long high = ((long) uuid.timeHiAndVersion() & 0x0FFF) << 48;
		long mid = (long) (uuid.timeMid()) << 32;
		long low = uuid.timeLow() & 0xFFFFFFFFL;
		long ts
			= high
			| mid
			| low;
		long tsMillis = ts / 10_000;
		Instant tsInst = TimeProviderV1.REFERENCE.plusMillis(tsMillis);
		long tsNanos = ts % (1_000_000 / 100);
		tsInst = tsInst.plusNanos(tsNanos);
		return tsInst;
	}
}
