# About

The electrologic UUID library is an extensible library for processing UUIDs.
It has been created as the UUID class bundled in the JRE is not capable of generating the new variants described in this [RFC draft](https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html).
While only the [RFC 4122](https://www.rfc-editor.org/rfc/rfc4122) variant is implemented, other variants could be added if there is the need for it.

# Usage

This library provides functionality to read and generate UUIDs.

## Reading UUIDs

There are static functions in the UUID and StandardUUID class to instantiate UUIDs from encoded form and from raw bytes.
The specialized class StandardUUID is only needed if the field properties need to be read.
For simple serialization, deserialization and comparison, the UUID class is sufficient.
When reading a UUID with the UUID class, also the specialiced variant is emitted, however the result must be cast.

```java
// read a UUID from a string
UUID u1 = UUID.parseHex("123e4567-e89b-12d3-a456-426655440000");
// read a UUID from a string asserting the variant as RFC 4122
StandardUUID u2 = StandardUUID.parseHex("123e4567-e89b-12d3-a456-426655440000");

// instantiate a UUID from a byte array
UUID u4 = UUID.fromBytes(octetBytesArray);
StandardUUID u4 = StandardUUID.fromBytes(octetBytesArray);

// casting is fine if the variant is RFC 4122
StandardUUID u5 = (StandardUUID) UUID.parseHex("e22ac190-6b94-4eab-88fd-f620e91144c2");
// but it raises a class cast exception if not
try {
	StandardUUID u5 = (StandardUUID) UUID.parseHex("00000000-0000-0000-0000-000000000000");
} catch (ClassCastException e) {
	System.err.println("This is not a RFC 4122 UUID");
}
```

## Generating UUIDs

The library provides two ways of generating UUIDs.

The simplest variant is by using the static methods in the UUID or the StandardUUID class.
Both classes emit the same objects, but the StandardUUID class allows to read the field properties.
```java
// generate a time based UUID (V1)
UUID u1 = UUID.createTimeV1();
// generate a random UUID (V4)
UUID u4 = UUID.createRandom();
// create name based UUID with SHA1 hashing (v5)
UUID u5 = UUID.createNameBased(UUID.NAMESPACE_DNS, "www.example.com");
// generate a ordered time based UUID (V7)
UUID u1 = UUID.createTimeV7();
```

A more advances method of generating UUIDs is by using the respective supplier classes.
They allow to make some modifications to the parameters, such as field size of the collision counter in time based UUIDs.
Please refer to the javadoc for details on the available parameters.

```java
// create a supplier for time based UUIDs
TimeV7Supplier s = new TimeV7Supplier();
// create a uuid
UUID u1 = s.get();
// create an infinite stream of uuids and put the first 1000 into a list
List<? extends UUID> uuids = s.toStream()
	.limit(1000)
	.toList();
```

# License

This software is released under the GNU LGPG license.
See [LICENSE](LICENSE) for details.
