package com.weelgo.core;

import java.util.UUID;

public class UuidGenerator implements IUuidGenerator {

	@Override
	public String generateUuid() {
		return generateUUIDObj().toString();
	}

	@Override
	public String generateUuid(int nbChar) {
		char[] uuidChar = generateUuid().toCharArray();
		String newUUID = "";
		for (int i = 0; i < nbChar; i++) {
			newUUID += uuidChar[i];
		}

		return newUUID;
	}

	public UUID generateUUIDObj() {
		UUID id = UUID.randomUUID();
		return id;
	}
}
