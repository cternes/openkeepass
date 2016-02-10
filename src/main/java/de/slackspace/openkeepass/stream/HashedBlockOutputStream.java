package de.slackspace.openkeepass.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashedBlockOutputStream extends OutputStream {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

	private OutputStream baseStream;
	private int bufferPos = 0;
	private byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	private long bufferIndex = 0;

	public HashedBlockOutputStream(OutputStream os) {
		baseStream = os;
	}

	@Override
	public void write(int oneByte) throws IOException {
		byte[] buf = new byte[1];
		buf[0] = (byte) oneByte;
		write(buf, 0, 1);
	}

	@Override
	public void close() throws IOException {
		if (bufferPos != 0) {
			// Write remaining buffered amount
			writeHashedBlock();
		}

		// Write terminating block
		writeHashedBlock();

		flush();
		baseStream.close();
	}

	@Override
	public void flush() throws IOException {
		baseStream.flush();
	}

	@Override
	public void write(byte[] b, int offset, int count) throws IOException {
		while (count > 0) {
			if (bufferPos == buffer.length) {
				writeHashedBlock();
			}

			int copyLen = Math.min(buffer.length - bufferPos, count);

			System.arraycopy(b, offset, buffer, bufferPos, copyLen);

			offset += copyLen;
			bufferPos += copyLen;

			count -= copyLen;
		}
	}

	private void writeHashedBlock() throws IOException {
		writeUInt(bufferIndex);
		bufferIndex++;

		if (bufferPos > 0) {
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				throw new IOException("SHA-256 not implemented here.", e);
			}

			byte[] hash;
			md.update(buffer, 0, bufferPos);
			hash = md.digest();

			baseStream.write(hash);

		} else {
			// Write 32-bits of zeros
			writeLong(0L);
			writeLong(0L);
			writeLong(0L);
			writeLong(0L);
		}

		writeInt(bufferPos);

		if (bufferPos > 0) {
			baseStream.write(buffer, 0, bufferPos);
		}

		bufferPos = 0;

	}

	@Override
	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	public void writeUInt(long uint) throws IOException {
		baseStream.write(writeIntBuf((int) uint));
	}

	public void writeInt(int val) throws IOException {
		byte[] buf = new byte[4];
		writeInt(val, buf, 0);

		baseStream.write(buf);
	}

	public byte[] writeIntBuf(int val) {
		byte[] buf = new byte[4];
		writeInt(val, buf, 0);

		return buf;
	}

	public void writeInt(int val, byte[] buf, int offset) {
		buf[offset + 0] = (byte) (val & 0xFF);
		buf[offset + 1] = (byte) ((val >>> 8) & 0xFF);
		buf[offset + 2] = (byte) ((val >>> 16) & 0xFF);
		buf[offset + 3] = (byte) ((val >>> 24) & 0xFF);
	}

	public void writeLong(long val) throws IOException {
		byte[] buf = new byte[8];

		writeLong(val, buf, 0);
		baseStream.write(buf);
	}

	public void writeLong(long val, byte[] buf, int offset) {
		buf[offset + 0] = (byte) (val & 0xFF);
		buf[offset + 1] = (byte) ((val >>> 8) & 0xFF);
		buf[offset + 2] = (byte) ((val >>> 16) & 0xFF);
		buf[offset + 3] = (byte) ((val >>> 24) & 0xFF);
		buf[offset + 4] = (byte) ((val >>> 32) & 0xFF);
		buf[offset + 5] = (byte) ((val >>> 40) & 0xFF);
		buf[offset + 6] = (byte) ((val >>> 48) & 0xFF);
		buf[offset + 7] = (byte) ((val >>> 56) & 0xFF);
	}
}
