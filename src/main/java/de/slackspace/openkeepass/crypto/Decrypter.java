package de.slackspace.openkeepass.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.stream.SafeInputStream;
import de.slackspace.openkeepass.util.StreamUtils;

public class Decrypter {

	public byte[] decryptDatabase(byte[] password, KeePassHeader header, byte[] database) throws IOException {
		byte[] aesKey = createAesKey(password, header);

		return processDatabaseEncryption(false, database, header, aesKey);
	}

	public byte[] encryptDatabase(byte[] password, KeePassHeader header, byte[] database) throws IOException {
		byte[] aesKey = createAesKey(password, header);

		return processDatabaseEncryption(true, database, header, aesKey);
	}

	private byte[] processDatabaseEncryption(boolean encrypt, byte[] database, KeePassHeader header, byte[] aesKey)
			throws IOException {
		byte[] metaData = new byte[KeePassHeader.VERSION_SIGNATURE_LENGTH + header.getHeaderSize()];
		SafeInputStream inputStream = new SafeInputStream(new BufferedInputStream(new ByteArrayInputStream(database)));
		inputStream.readSafe(metaData);

		byte[] payload = StreamUtils.toByteArray(inputStream);
		byte[] processedPayload;
		if (encrypt) {
			processedPayload = Aes.encrypt(aesKey, header.getEncryptionIV(), payload);
		} else {
			processedPayload = Aes.decrypt(aesKey, header.getEncryptionIV(), payload);
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		output.write(metaData);
		output.write(processedPayload);

		return output.toByteArray();
	}

	private byte[] createAesKey(byte[] password, KeePassHeader header) {
		byte[] hashedPwd = Sha256.hash(password);

		byte[] transformedPwd = Aes.transformKey(header.getTransformSeed(), hashedPwd, header.getTransformRounds());
		byte[] transformedHashedPwd = Sha256.hash(transformedPwd);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(header.getMasterSeed(), 0, 32);
		stream.write(transformedHashedPwd, 0, 32);

		return Sha256.hash(stream.toByteArray());
	}
}
