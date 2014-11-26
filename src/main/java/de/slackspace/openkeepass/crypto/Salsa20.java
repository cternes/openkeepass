package de.slackspace.openkeepass.crypto;

import java.io.UnsupportedEncodingException;

import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class Salsa20 implements ProtectedStringCrypto {
	
	private static final String SALSA20IV = "E830094B97205D2A";
	
	private Salsa20Engine salsa20Engine;
	
	private Salsa20() {	}
	
	private void initialize(byte[] protectedStreamKey) {
		byte[] salsaKey = Sha256.hash(protectedStreamKey);
		salsa20Engine = new Salsa20Engine();
		salsa20Engine.init(true, new ParametersWithIV(new KeyParameter(salsaKey), Hex.decode(SALSA20IV)));
	}
	
	public static Salsa20 createInstance(byte[] protectedStreamKey) {
		Salsa20 salsa20 = new Salsa20();
		salsa20.initialize(protectedStreamKey);

		return salsa20;
	}

	public String decrypt(String protectedPassword) {
		byte[] protectedPwdBuffer = Base64.decode(protectedPassword.getBytes());
		byte[] plainTextPassword = new byte[protectedPwdBuffer.length];
		
		salsa20Engine.processBytes(protectedPwdBuffer, 0, protectedPwdBuffer.length, plainTextPassword, 0);
		
		try { 
			return new String(plainTextPassword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException("The enconding is not supported");
		}
	}
	
}
