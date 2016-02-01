package de.slackspace.openkeepass.stream;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.StreamUtils;

public class HashedBlockInputStream extends InputStream {

	private final static int HASH_SIZE = 32;

	private InputStream baseStream;
	private int bufferPos = 0;
	private byte[] buffer = new byte[0];
	private long bufferIndex = 0;
	private boolean atEnd = false;

	public HashedBlockInputStream(InputStream is) {
		baseStream = is;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int offset, int length) throws IOException {
		if (atEnd)
			return -1;

		int remaining = length;

		while (remaining > 0) {
			if (bufferPos == buffer.length) {
				// Get more from the source into the buffer
				if (!readHashedBlock()) {
					return length - remaining;
				}

			}

			// Copy from buffer out
			int copyLen = Math.min(buffer.length - bufferPos, remaining);

			System.arraycopy(buffer, bufferPos, b, offset, copyLen);

			offset += copyLen;
			bufferPos += copyLen;

			remaining -= copyLen;
		}

		return length;
	}

	private boolean readHashedBlock() throws IOException {
		if (atEnd)
			return false;

		bufferPos = 0;

		long index = ByteUtils.readUnsignedInt(baseStream);
		if (index != bufferIndex) {
			throw new IOException("Invalid data format");
		}
		bufferIndex++;

		byte[] storedHash = new byte[32];
		StreamUtils.read(baseStream, storedHash);
		if (storedHash == null || storedHash.length != HASH_SIZE) {
			throw new IOException("Invalid data format");
		}

		int bufferSize = ByteUtils.readInt(baseStream);
		if (bufferSize < 0) {
			throw new IOException("Invalid data format");
		}

		if (bufferSize == 0) {
			for (int hash = 0; hash < HASH_SIZE; hash++) {
				if (storedHash[hash] != 0) {
					throw new IOException("Invalid data format");
				}
			}

			atEnd = true;
			buffer = new byte[0];
			return false;
		}

		buffer = new byte[bufferSize];
		StreamUtils.read(baseStream, buffer);
		if (buffer == null || buffer.length != bufferSize) {
			throw new IOException("Invalid data format");
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("SHA-256 not implemented here.");
		}

		byte[] computedHash = md.digest(buffer);
		if (computedHash == null || computedHash.length != HASH_SIZE) {
			throw new IOException("Hash wrong size");
		}

		if (!Arrays.equals(storedHash, computedHash)) {
			throw new IOException("Hashes didn't match.");
		}

		return true;
	}

	@Override
	public long skip(long n) throws IOException {
		return 0;
	}

	@Override
	public int read() throws IOException {
		if (atEnd)
			return -1;

		if (bufferPos == buffer.length) {
			if (!readHashedBlock())
				return -1;
		}

		int output = buffer[bufferPos];
		bufferPos++;

		return output;
	}

	@Override
	public void close() throws IOException {
		baseStream.close();
	}
}
