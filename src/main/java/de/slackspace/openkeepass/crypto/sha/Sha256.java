package de.slackspace.openkeepass.crypto.sha;

public class Sha256 extends AbstractSha {

    private Sha256() {}

    public static Sha256 getInstance() {
        return new Sha256();
    }

    @Override
    protected ShaAlgorithm getShaAlgorithm() {
        return ShaAlgorithm.SHA_256;
    }
}
