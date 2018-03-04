package de.slackspace.openkeepass.stream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream extends InputStream implements DataInput {

    private DataInputStream dataInputStream;
    private InputStream inputStream;
    private byte[] buffer;

    public LittleEndianDataInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.dataInputStream = new DataInputStream(inputStream);
        buffer = new byte[8];
    }

    @Override
    public int available() throws IOException {
        return dataInputStream.available();
    }

    public short readShort() throws IOException {
        dataInputStream.readFully(buffer, 0, 2);
        return (short) ((buffer[1] & 0xff) << 8 |
                (buffer[0] & 0xff));
    }

    public int readUnsignedShort() throws IOException {
        dataInputStream.readFully(buffer, 0, 2);
        return ((buffer[1] & 0xff) << 8 |
                (buffer[0] & 0xff));
    }

    public char readChar() throws IOException {
        dataInputStream.readFully(buffer, 0, 2);
        return (char) ((buffer[1] & 0xff) << 8 | (buffer[0] & 0xff));
    }

    public int readInt() throws IOException {
        dataInputStream.readFully(buffer, 0, 4);
        return (buffer[3]) << 24 |
                (buffer[2] & 0xff) << 16 |
                (buffer[1] & 0xff) << 8 |
                (buffer[0] & 0xff);
    }

    public long readLong() throws IOException {
        dataInputStream.readFully(buffer, 0, 8);
        return (long) (buffer[7]) << 56 |
                (long) (buffer[6] & 0xff) << 48 |
                (long) (buffer[5] & 0xff) << 40 |
                (long) (buffer[4] & 0xff) << 32 |
                (long) (buffer[3] & 0xff) << 24 |
                (long) (buffer[2] & 0xff) << 16 |
                (long) (buffer[1] & 0xff) << 8 |
                (long) (buffer[0] & 0xff);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        return inputStream.read(bytes, off, len);
    }

    public void readFully(byte[] bytes) throws IOException {
        dataInputStream.readFully(bytes, 0, bytes.length);
    }

    public void readFully(byte[] bytes, int off, int len) throws IOException {
        dataInputStream.readFully(bytes, off, len);
    }

    public int skipBytes(int n) throws IOException {
        return dataInputStream.skipBytes(n);
    }

    public boolean readBoolean() throws IOException {
        return dataInputStream.readBoolean();
    }

    public byte readByte() throws IOException {
        return dataInputStream.readByte();
    }

    public int read() throws IOException {
        return inputStream.read();
    }

    public int readUnsignedByte() throws IOException {
        return dataInputStream.readUnsignedByte();
    }

    public String readUTF() throws IOException {
        return dataInputStream.readUTF();
    }

    @Deprecated
    public String readLine() throws IOException {
        return dataInputStream.readLine();
    }

    @Override
    public void close() throws IOException {
        dataInputStream.close();
    }
}
