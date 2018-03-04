package de.slackspace.openkeepass.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

import de.slackspace.openkeepass.crypto.sha.Sha256;
import de.slackspace.openkeepass.domain.KdfDictionary;

public class Argon2Test {

    @Test
    public void whenKeyAndDictIsProvidedShouldTransformKey() {
        // arrange
        byte[] kdfHeader = ByteUtils.fromHexString(
                "00014205000000245555494410000000ef636ddf8c29444b91f7a9a403e30a0c040100000056040000001300000005010000004908000000020000000000000005010000004d0800000000001000000000000401000000500400000002000000420100000053200000007ea16ccbf5f48cb5f77b01a9192123164c5f5f5245a10e5f9c848f47f0c93a4c00");

        KdfDictionary dictionary = new KdfDictionary(kdfHeader);

        String password = "123";
        byte[] hashedPassword = Sha256.getInstance().hash(password.getBytes());
        byte[] key = Sha256.getInstance().hash(hashedPassword);

        // act
        byte[] transformedKey = Argon2.transformKey(key, dictionary);

        // assert
        assertThat(transformedKey,
                is(ByteUtils.fromHexString("4da9a5973c920fe610dfcda20c74d3140ca7eca13e47e5e9957e5d823f5af9df")));
    }
}
