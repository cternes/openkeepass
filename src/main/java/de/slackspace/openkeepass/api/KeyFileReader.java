package de.slackspace.openkeepass.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.bouncycastle.util.encoders.Base64;

import de.slackspace.openkeepass.crypto.Sha256;
import de.slackspace.openkeepass.domain.KeyFile;
import de.slackspace.openkeepass.xml.KeyFileXmlParser;

public class KeyFileReader {

	private static final String UTF_8 = "UTF-8";
	private static final String MSG_UTF8_NOT_SUPPORTED = "The encoding UTF-8 is not supported";

	protected KeyFileXmlParser keyFileXmlParser = new KeyFileXmlParser();

	public byte[] readKeyFile(InputStream keyFileStream) {
		try {
			byte[] protectedBuffer = parseKeyFile(keyFileStream);
			protectedBuffer = hashKeyFileIfNecessary(protectedBuffer);

			return protectedBuffer;
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(MSG_UTF8_NOT_SUPPORTED, e);
		}
	}

	private byte[] parseKeyFile(InputStream keyFileStream) throws UnsupportedEncodingException {
		KeyFile keyFile = keyFileXmlParser.fromXml(keyFileStream);
		byte[] protectedBuffer = Base64.decode(keyFile.getKey().getData().getBytes(UTF_8));
		return protectedBuffer;
	}

	private byte[] hashKeyFileIfNecessary(byte[] protectedBuffer) {
		if (protectedBuffer.length != 32) {
			protectedBuffer = Sha256.hash(protectedBuffer);
		}
		return protectedBuffer;
	}
}
