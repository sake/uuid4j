package ellog.uuid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;

public class CloneTest {
	@Test
	void cloneV1() {
		TimeV1Supplier s1 = new TimeV1Supplier();
		TimeV1Supplier s2 = s1.clone();
		assertNotSame(s1, s2);
	}
	@Test
	void cloneV4() {
		Version4Supplier s1 = new Version4Supplier();
		Version4Supplier s2 = s1.clone();
		assertNotSame(s1, s2);
	}
	@Test
	void cloneV6() {
		TimeV6Supplier s1 = new TimeV6Supplier();
		TimeV6Supplier s2 = s1.clone();
		assertNotSame(s1, s2);
	}
	@Test
	void cloneV7() {
		TimeV7Supplier s1 = new TimeV7Supplier();
		TimeV7Supplier s2 = s1.clone();
		assertNotSame(s1, s2);
	}
	@Test
	void cloneNameBased() {
		NameBasedSupplier s1 = NameBasedSupplier.version3(NameBasedSupplier.NS_URL);
		NameBasedSupplier s2 = s1.clone();
		assertNotSame(s1, s2);
	}
}
