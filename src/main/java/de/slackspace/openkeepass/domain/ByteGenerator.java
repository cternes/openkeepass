package de.slackspace.openkeepass.domain;

public interface ByteGenerator {

    public byte[] getRandomBytes(int numBytes);

}
