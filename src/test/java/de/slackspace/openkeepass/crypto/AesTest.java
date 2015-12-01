package de.slackspace.openkeepass.crypto;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class AesTest {

	@Test(expected=IllegalArgumentException.class)
	public void whenRoundsIsNegativeShouldThrowException() {
		Aes.transformKey(new byte[0], new byte[0], -1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void whenKeyIsNullShouldThrowException() {
		Aes.transformKey(null, new byte[0], 1000);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void whenDataIsNullShouldThrowException() {
		Aes.transformKey(new byte[0], null, 1000);
	}
	
	@Test
	public void shouldTransformKeyWith1000Rounds() {
		byte[] key = ByteUtils.hexStringToByteArray("3ecaacce890184af4fe7d6f6369dd14eb4a8a9641fefe346c9fbacb03c82a7c4");
		byte[] data = ByteUtils.hexStringToByteArray("2e99758548972a8e8822ad47fa1017ff72f06f3ff6a016851f45c398732bc50c");
		
		byte[] transformedKey = Aes.transformKey(key, data, 1000);

		Assert.assertTrue("Hash must be 7ca367977502be1e8bb5b143706c3455cf1fe42458c80b1055db96113b9c3fec", Arrays.equals(ByteUtils.hexStringToByteArray("7ca367977502be1e8bb5b143706c3455cf1fe42458c80b1055db96113b9c3fec"), transformedKey));
	}
	
	@Test
	public void shouldTransformKeyWith6000Rounds() {
		byte[] key = ByteUtils.hexStringToByteArray("3ecaacce890184af4fe7d6f6369dd14eb4a8a9641fefe346c9fbacb03c82a7c4");
		byte[] data = ByteUtils.hexStringToByteArray("2e99758548972a8e8822ad47fa1017ff72f06f3ff6a016851f45c398732bc50c");
		
		byte[] transformedKey = Aes.transformKey(key, data, 6000);

		Assert.assertTrue("Hash must be 8ad8a3addb033449fbee13c3fbf1ad74b9760d6c21048f08027b2ddc8adef20a", Arrays.equals(ByteUtils.hexStringToByteArray("8ad8a3addb033449fbee13c3fbf1ad74b9760d6c21048f08027b2ddc8adef20a"), transformedKey));
	}
}
