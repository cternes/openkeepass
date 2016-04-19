package de.slackspace.openkeepass.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.bouncycastle.util.encoders.Base64;

import de.slackspace.openkeepass.crypto.Sha256;
import de.slackspace.openkeepass.domain.KeyFile;
import de.slackspace.openkeepass.exception.KeyFileUnreadableException;
import de.slackspace.openkeepass.util.StreamUtils;
import de.slackspace.openkeepass.xml.KeyFileXmlParser;

public class KeyFileReader {

	private static final String UTF_8 = "UTF-8";
	private static final String MSG_UTF8_NOT_SUPPORTED = "The encoding UTF-8 is not supported";

	protected KeyFileXmlParser keyFileXmlParser = new KeyFileXmlParser();

	public byte[] readKeyFile(InputStream keyFileStream) {
		byte[] keyFileContent = readKeyFileStream(keyFileStream);
		KeyFile keyFile = keyFileXmlParser.fromXml(keyFileContent);
		
		if(!keyFile.isXmlFile()) {
			return readBinaryKeyFile(keyFileContent);
		}
		
		try {
			byte[] protectedBuffer = Base64.decode(keyFile.getKey().getData().getBytes(UTF_8));
			return hashKeyFileIfNecessary(protectedBuffer);
		}
		catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(MSG_UTF8_NOT_SUPPORTED, e);
		}
	}

	private byte[] readKeyFileStream(InputStream keyFileStream) {
		try {
			return StreamUtils.toByteArray(keyFileStream);
		}
		catch (IOException e) {
			throw new KeyFileUnreadableException("Could not read key file", e);
		}
	}

	private byte[] readBinaryKeyFile(byte[] keyFile) {
		byte[] protectedBuffer;
		
		int length = keyFile.length;
		if (length == 64) {
			protectedBuffer = Base64.decode(keyFile);
		}

		protectedBuffer = hashKeyFileIfNecessary(keyFile);
		
		return protectedBuffer;
	}

	private byte[] hashKeyFileIfNecessary(byte[] protectedBuffer) {
		if (protectedBuffer.length != 32) {
			protectedBuffer = Sha256.hash(protectedBuffer);
		}
		return protectedBuffer;
	}
}
