package de.slackspace.openkeepass.crypto.sha;

public class Sha256 extends AbstractSha {

    private Sha256() {
        super(getDigestInstance(ShaAlgorithm.SHA_256));
    }

    public static Sha256 getInstance() {
        return new Sha256();
    }
}
