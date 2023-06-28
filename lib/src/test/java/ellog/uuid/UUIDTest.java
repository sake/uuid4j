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

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class UUIDTest {

    @Test
	void readNil() {
        String nul = "00000000-0000-0000-0000-000000000000";
		UUID uuid = UUID.parseHex(nul);
		assertEquals(nul, uuid.toString());
		assertEquals("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", uuid.toStringBinary());
		assertEquals("0", uuid.toStringDecimal());
		assertEquals(UUID.NIL_UUID, uuid);
		assertEquals("urn:uuid:" + nul, uuid.toUrn().toString());
		assertEquals("urn:oid:0", uuid.toOidUrn().toString());
    }

	@Test
	void readMax() {
		String max = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
		UUID uuid = UUID.parseHex(max);
		assertEquals(max.toLowerCase(), uuid.toString());
		assertEquals("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111", uuid.toStringBinary());
		assertEquals("-1", uuid.toStringDecimal());
		assertEquals(UUID.MAX_UUID, uuid);
		assertEquals("urn:oid:-1", uuid.toOidUrn().toString());
	}

	@Test
	void comparison() {
		UUID n1 = UUID.parseHex("00000000-0000-0000-0000-000000000000");
		UUID n2 = UUID.parseHex("00000000-0000-0000-0000-000000000000");
		UUID med1 = UUID.parseHex("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFF0");
		UUID med2 = UUID.parseHex("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFF0");
		UUID m1 = UUID.parseHex("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
		UUID m2 = UUID.parseHex("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");

		assertEquals(n1, n2);
		assertEquals(med1, med2);
		assertEquals(m1, m2);
		assertNotEquals(n1, med1);
		assertNotEquals(n1, m1);
		assertNotEquals(n2, med1);

		assertTrue(n1.compareTo(med1) < 0);
		assertTrue(med1.compareTo(m1) < 0);
		assertTrue(n1.compareTo(m1) < 0);

		assertTrue(m1.compareTo(med1) > 0);
		assertTrue(med1.compareTo(n1) > 0);
		assertTrue(m1.compareTo(n1) > 0);

		assertTrue(n1.compareTo(n2) == 0);
		assertTrue(med1.compareTo(med2) == 0);
		assertTrue(m1.compareTo(m2) == 0);
	}

	@Test
	void serialization() throws IOException, ClassNotFoundException {
		UUID u1 = UUID.parseHex("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
		UUID reread1 = serializeDeserialize(u1);
		assertEquals(u1, reread1);

		UUID u2 = UUID.parseHex("89ABCDEF-4567-1123-B234-CBA987654321");
		UUID reread2 = serializeDeserialize(u2);
		assertEquals(u2, reread2);
	}

	UUID serializeDeserialize(UUID input) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(out);
		oout.writeObject(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		ObjectInputStream oin = new ObjectInputStream(in);
		UUID output = (UUID) oin.readObject();
		return output;
	}

}
