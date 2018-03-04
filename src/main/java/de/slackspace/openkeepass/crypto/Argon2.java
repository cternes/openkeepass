package de.slackspace.openkeepass.crypto;

import com.kosprov.jargon2.api.Jargon2;
import com.kosprov.jargon2.api.Jargon2.Hasher;
import com.kosprov.jargon2.api.Jargon2.Type;
import com.kosprov.jargon2.api.Jargon2.Version;

import de.slackspace.openkeepass.domain.KdfDictionary;

public class Argon2 {

    private static final int MEMORY_SIZE = 1024;
    private static final int HASH_LENGTH = 32;

    private Argon2() {}

    public static byte[] transformKey(byte[] key, KdfDictionary dictionary) {
        Version version = getVersion(dictionary);
        int parallelism = dictionary.getParallelism();
        int iterations = (int) dictionary.getIterations();
        int memory = (int) dictionary.getMemory();
        byte[] salt = dictionary.getSalt();

        Hasher argon2 = Jargon2.jargon2Hasher()
                .type(Type.ARGON2d)
                .version(version)
                .parallelism(parallelism)
                .timeCost(iterations)
                .memoryCost(memory / MEMORY_SIZE)
                .salt(salt)
                .hashLength(HASH_LENGTH);

        return argon2.password(key).rawHash();
    }

    private static Version getVersion(KdfDictionary dictionary) {
        int version = dictionary.getVersion();

        if (version == Version.V10.getValue()) {
            return Version.V10;
        }

        return Version.V13;
    }
}
