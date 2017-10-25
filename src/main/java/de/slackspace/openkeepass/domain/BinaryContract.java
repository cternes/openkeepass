package de.slackspace.openkeepass.domain;

public interface BinaryContract {

    int getId();

    boolean isCompressed();

    byte[] getData();
}
