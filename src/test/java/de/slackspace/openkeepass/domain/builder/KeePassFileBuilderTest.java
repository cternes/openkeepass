package de.slackspace.openkeepass.domain.builder;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.KeePassFile;

public class KeePassFileBuilderTest {

	@Test
	public void shouldBuildKeePassFileWithReasonableDefaults() {
		KeePassFile keePassFile = new KeePassFileBuilder("testDB").build();
		
		Assert.assertEquals("testDB", keePassFile.getMeta().getDatabaseName());
		Assert.assertEquals("KeePass", keePassFile.getMeta().getGenerator());
		Assert.assertNotNull(keePassFile.getRoot());
		Assert.assertEquals(1, keePassFile.getRoot().getGroups().size());
	}
}
