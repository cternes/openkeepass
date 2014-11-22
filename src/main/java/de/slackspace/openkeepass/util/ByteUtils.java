package de.slackspace.openkeepass.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {

	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i < bytes.length; i++){
			buffer.append(Character.forDigit((bytes[i] >> 4) & 0xF, 16));
			buffer.append(Character.forDigit((bytes[i] & 0xF), 16));
		}
		
		return buffer.toString();
	}
	
	public static int toUnsignedInt(int value) {
		return value & 0xFF;
	}
	
	public static int readInt(InputStream inputStream) throws IOException {
		byte[] bytesToRead =  new byte[4];
		inputStream.read(bytesToRead);
		
		ByteBuffer buffer = ByteBuffer.wrap(bytesToRead);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		return buffer.getInt();
	}
	
	public static int readUnsignedInt(InputStream inputStream) throws IOException {
		int value = readInt(inputStream);
		
		return ByteUtils.toUnsignedInt(value);
	}
}
