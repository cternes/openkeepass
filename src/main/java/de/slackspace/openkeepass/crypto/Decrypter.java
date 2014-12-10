package de.slackspace.openkeepass.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.util.StreamUtils;

public class Decrypter {

	public byte[] decryptDatabase(String password, KeePassHeader header, byte[] database) throws IOException {
		byte[] aesKey = createAesKey(password, header);
		
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(database));
		bufferedInputStream.skip(KeePassDatabase.VERSION_SIGNATURE_LENGTH + header.getHeaderSize()); 
		
		byte[] payload = StreamUtils.toByteArray(bufferedInputStream);
		
		return Aes.decrypt(aesKey, header.getEncryptionIV(), payload);
	}
	
	private byte[] createAesKey(String password, KeePassHeader header) {
		byte[] hashedPwd = Sha256.hash(password);
		byte[] doubleHashedPwd = Sha256.hash(hashedPwd);
		
		byte[] transformedPwd = Aes.transformKey(header.getTransformSeed(), doubleHashedPwd, header.getTransformRounds());
		
		byte[] transformedHashedPwd = Sha256.hash(transformedPwd);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(header.getMasterSeed(), 0, 32);
		stream.write(transformedHashedPwd, 0, 32);
		
		byte[] aesKey = Sha256.hash(stream.toByteArray());
		return aesKey;
	}
}
