package de.slackspace.openkeepass.crypto.sha;

public class Sha512 extends AbstractSha {

    private Sha512() {
        super(getDigestInstance(ShaAlgorithm.SHA_512));
    }

    public static Sha512 getInstance() {
        return new Sha512();
    }
}
