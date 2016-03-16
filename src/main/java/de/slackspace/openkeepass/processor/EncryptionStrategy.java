package de.slackspace.openkeepass.processor;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

public class EncryptionStrategy implements ProtectionStrategy {

    private ProtectedStringCrypto crypto;

    public EncryptionStrategy(ProtectedStringCrypto crypto) {
        this.crypto = crypto;
    }

    @Override
    public String apply(String value) {
        return crypto.encrypt(value);
    }
}
