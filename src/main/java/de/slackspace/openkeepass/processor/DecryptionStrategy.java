package de.slackspace.openkeepass.processor;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

public class DecryptionStrategy implements ProtectionStrategy {

    private ProtectedStringCrypto crypto;

    public DecryptionStrategy(ProtectedStringCrypto crypto) {
        this.crypto = crypto;
    }

    @Override
    public String apply(String value) {
        return crypto.decrypt(value);
    }

}
