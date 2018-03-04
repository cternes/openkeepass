package de.slackspace.openkeepass.crypto.sha;

public class Sha512 extends AbstractSha {

    private Sha512() {}

    public static Sha512 getInstance() {
        return new Sha512();
    }

    @Override
    protected ShaAlgorithm getShaAlgorithm() {
        return ShaAlgorithm.SHA_512;
    }
}
