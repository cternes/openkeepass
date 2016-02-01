package de.slackspace.openkeepass.crypto;

import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(database));
		bufferedInputStream.read(metaData);

		byte[] payload = StreamUtils.toByteArray(bufferedInputStream);
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
