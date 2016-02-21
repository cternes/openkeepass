package de.slackspace.openkeepass.crypto;

public class CryptoInformation {

	private int versionSignatureLength;

	private byte[] masterSeed;

	private byte[] transformSeed;

	private long transformRounds;

	public byte[] getMasterSeed() {
		return masterSeed;
	}

	public void setMasterSeed(byte[] masterSeed) {
		this.masterSeed = masterSeed;
	}

	public byte[] getTransformSeed() {
		return transformSeed;
	}

	public void setTransformSeed(byte[] transformSeed) {
		this.transformSeed = transformSeed;
	}

	public long getTransformRounds() {
		return transformRounds;
	}

	public void setTransformRounds(long transformRounds) {
		this.transformRounds = transformRounds;
	}

	public int getHeaderSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte[] getEncryptionIV() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getVersionSignatureLength() {
		return versionSignatureLength;
	}

	public void setVersionSignatureLength(int versionSignatureLength) {
		this.versionSignatureLength = versionSignatureLength;
	}

}
