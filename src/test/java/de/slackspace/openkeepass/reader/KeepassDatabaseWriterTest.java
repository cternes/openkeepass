package de.slackspace.openkeepass.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.CompressionAlgorithm;
import de.slackspace.openkeepass.domain.CrsAlgorithm;
import de.slackspace.openkeepass.domain.CustomIcon;
import de.slackspace.openkeepass.domain.CustomIconBuilder;
import de.slackspace.openkeepass.domain.CustomIcons;
import de.slackspace.openkeepass.domain.CustomIconsBuilder;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.GroupBuilder;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.domain.Meta;
import de.slackspace.openkeepass.domain.MetaBuilder;
import de.slackspace.openkeepass.domain.zipper.GroupZipper;
import de.slackspace.openkeepass.processor.DecryptionStrategy;
import de.slackspace.openkeepass.processor.ProtectedValueProcessor;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.xml.KeePassDatabaseXmlParser;

public class KeepassDatabaseWriterTest {

	private byte[] protectedStreamKey = ByteUtils
			.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");

	@Test
	public void whenWritingDatabaseFileShouldBeAbleToReadItAlso() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new KeePassDatabaseXmlParser().fromXml(fileInputStream);
		new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(Salsa20.createInstance(protectedStreamKey)), keePassFile);

		FileOutputStream file = new FileOutputStream("target/test-classes/writeDatabase.kdbx");
		KeePassDatabase.write(keePassFile, "abcdefg", file);

		KeePassDatabase database = KeePassDatabase.getInstance("target/test-classes/writeDatabase.kdbx");
		KeePassHeader header = database.getHeader();

		Assert.assertEquals(CompressionAlgorithm.Gzip, header.getCompression());
		Assert.assertEquals(CrsAlgorithm.Salsa20, header.getCrsAlgorithm());
		Assert.assertEquals(8000, header.getTransformRounds());

		KeePassFile openDatabase = database.openDatabase("abcdefg");

		Entry sampleEntry = openDatabase.getEntryByTitle("Sample Entry");
		Assert.assertEquals("User Name", sampleEntry.getUsername());
		Assert.assertEquals("Password", sampleEntry.getPassword());

		Entry sampleEntryTwo = openDatabase.getEntryByTitle("Sample Entry #2");
		Assert.assertEquals("Michael321", sampleEntryTwo.getUsername());
		Assert.assertEquals("12345", sampleEntryTwo.getPassword());
	}

	@Test
	public void shouldCreateNewDatabaseFile() throws FileNotFoundException {
		Entry entryOne = new EntryBuilder("First entry").username("Carl").password("Carls secret").build();

		KeePassFile keePassFile = new KeePassFileBuilder("testDB").addTopEntries(entryOne).build();

		String dbFilename = "target/test-classes/writeNewDatabase.kdbx";
		KeePassDatabase.write(keePassFile, "abc", new FileOutputStream(dbFilename));

		KeePassDatabase keePassDb = KeePassDatabase.getInstance(dbFilename);
		KeePassFile database = keePassDb.openDatabase("abc");
		Entry entryByTitle = database.getEntryByTitle("First entry");

		Assert.assertEquals(entryOne.getTitle(), entryByTitle.getTitle());
	}

	@Test
	public void shouldBuildKeePassFileWithTreeStructure() throws FileNotFoundException {
		/*
		 * Should create the following structure:
		 *
		 * Root | |-- First entry (E) |-- Banking (G) | |-- Internet (G) | |--
		 * Shopping (G) |-- Second entry (E)
		 *
		 */
		Group root = new GroupBuilder().addEntry(new EntryBuilder("First entry").build())
				.addGroup(new GroupBuilder("Banking").build())
				.addGroup(new GroupBuilder("Internet")
						.addGroup(
								new GroupBuilder("Shopping").addEntry(new EntryBuilder("Second entry").build()).build())
						.build())
				.build();

		KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB").addTopGroups(root).build();

		String dbFilename = "target/test-classes/writeTreeDB.kdbx";
		KeePassDatabase.write(keePassFile, "abc", new FileOutputStream(dbFilename));

		KeePassDatabase keePassDb = KeePassDatabase.getInstance(dbFilename);
		KeePassFile database = keePassDb.openDatabase("abc");

		Assert.assertEquals("Banking", database.getTopGroups().get(0).getName());
		Assert.assertEquals("Internet", database.getTopGroups().get(1).getName());
		Assert.assertEquals("First entry", database.getTopEntries().get(0).getTitle());
		Assert.assertEquals("Shopping", database.getTopGroups().get(1).getGroups().get(0).getName());
		Assert.assertEquals("Second entry",
				database.getTopGroups().get(1).getGroups().get(0).getEntries().get(0).getTitle());
	}

	@Test
	public void shouldModifiyGroupInKeePassFile() throws FileNotFoundException {
		String password = "123456";
		KeePassDatabase keePassDb = KeePassDatabase.getInstance("target/test-classes/fullBlownDatabase.kdbx");
		KeePassFile database = keePassDb.openDatabase(password);

		Group group = database.getGroupByName("test");
		Group modifiedGroup = new GroupBuilder(group).name("test2").build();

		GroupZipper zipper = new KeePassFileBuilder(database).getZipper().down().right().right().right().right().down();
		KeePassFile modifiedDatabase = zipper.replace(modifiedGroup).close();

		String dbFilename = "target/test-classes/fullBlownDatabaseModified.kdbx";
		KeePassDatabase.write(modifiedDatabase, password, new FileOutputStream(dbFilename));
		KeePassFile databaseReadFromHdd = KeePassDatabase.getInstance(dbFilename).openDatabase(password);

		Assert.assertNotNull("Banking", databaseReadFromHdd.getGroupByName("test2"));
		Assert.assertEquals(2, databaseReadFromHdd.getGroupByName("test2").getEntries().size());
	}

	@Test
	public void shouldModifyMetadataAndRenameGeneralNodeThenWriteAndReadDatabase() throws FileNotFoundException {
		String password = "abcdefg";
		String originalDbFile = "target/test-classes/testDatabase.kdbx";
		String modifiedDbFile = "target/test-classes/modifiedtestDatabase2.kdbx";

		KeePassFile database = KeePassDatabase.getInstance(originalDbFile).openDatabase(password);

		// change db name
		Meta meta = new MetaBuilder(database.getMeta()).databaseName("differentName").build();

		// change general node
		GroupZipper zipper = new GroupZipper(database).down();
		Group renamedNode = new GroupBuilder(zipper.getNode()).name("Misc").build();
		KeePassFile modifiedKeepassFile = zipper.replace(renamedNode).replaceMeta(meta).close();

		// write
		KeePassDatabase.write(modifiedKeepassFile, password, modifiedDbFile);

		// read and assert
		KeePassFile readModifiedDb = KeePassDatabase.getInstance(modifiedDbFile).openDatabase(password);
		Assert.assertEquals("differentName", readModifiedDb.getMeta().getDatabaseName());
		Assert.assertEquals("Misc", readModifiedDb.getTopGroups().get(0).getName());
	}

	@Test
	public void shouldWriteDatabaseWithCustomIcon() throws FileNotFoundException {
		String base64Icon = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAMAAADXqc3KAAAB+FBMVEUAAAA/mUPidDHiLi5Cn0XkNTPmeUrkdUg/m0Q0pEfcpSbwaVdKskg+lUP4zA/iLi3msSHkOjVAmETdJSjtYFE/lkPnRj3sWUs8kkLeqCVIq0fxvhXqUkbVmSjwa1n1yBLepyX1xxP0xRXqUkboST9KukpHpUbuvRrzrhF/ljbwaljuZFM4jELaoSdLtElJrUj1xxP6zwzfqSU4i0HYnydMtUlIqUfywxb60AxZqEXaoifgMCXptR9MtklHpEY2iUHWnSjvvRr70QujkC+pUC/90glMuEnlOjVMt0j70QriLS1LtEnnRj3qUUXfIidOjsxAhcZFo0bjNDH0xxNLr0dIrUdmntVTkMoyfL8jcLBRuErhJyrgKyb4zA/5zg3tYFBBmUTmQTnhMinruBzvvhnxwxZ/st+Ktt5zp9hqota2vtK6y9FemNBblc9HiMiTtMbFtsM6gcPV2r6dwroseLrMrbQrdLGdyKoobKbo3Zh+ynrgVllZulTsXE3rV0pIqUf42UVUo0JyjEHoS0HmsiHRGR/lmRz/1hjqnxjvpRWfwtOhusaz0LRGf7FEfbDVmqHXlJeW0pbXq5bec3fX0nTnzmuJuWvhoFFhm0FtrziBsjaAaDCYWC+uSi6jQS3FsSfLJiTirCOkuCG1KiG+wSC+GBvgyhTszQ64Z77KAAAARXRSTlMAIQRDLyUgCwsE6ebm5ubg2dLR0byXl4FDQzU1NDEuLSUgC+vr6urq6ubb29vb2tra2tG8vLu7u7uXl5eXgYGBgYGBLiUALabIAAABsElEQVQoz12S9VPjQBxHt8VaOA6HE+AOzv1wd7pJk5I2adpCC7RUcHd3d3fXf5PvLkxheD++z+yb7GSRlwD/+Hj/APQCZWxM5M+goF+RMbHK594v+tPoiN1uHxkt+xzt9+R9wnRTZZQpXQ0T5uP1IQxToyOAZiQu5HEpjeA4SWIoksRxNiGC1tRZJ4LNxgHgnU5nJZBDvuDdl8lzQRBsQ+s9PZt7s7Pz8wsL39/DkIfZ4xlB2Gqsq62ta9oxVlVrNZpihFRpGO9fzQw1ms0NDWZz07iGkJmIFH8xxkc3a/WWlubmFkv9AB2SEpDvKxbjidN2faseaNV3zoHXvv7wMODJdkOHAegweAfFPx4G67KluxzottCU9n8CUqXzcIQdXOytAHqXxomvykhEKN9EFutG22p//0rbNvHVxiJywa8yS2KDfV1dfbu31H8jF1RHiTKtWYeHxUvq3bn0pyjCRaiRU6aDO+gb3aEfEeVNsDgm8zzLy9egPa7Qt8TSJdwhjplk06HH43ZNJ3s91KKCHQ5x4sw1fRGYDZ0n1L4FKb9/BP5JLYxToheoFCVxz57PPS8UhhEpLBVeAAAAAElFTkSuQmCC";

		UUID iconUuid = UUID.randomUUID();
		byte[] customPng = DatatypeConverter.parseBase64Binary(base64Icon);

		// build database
		CustomIcon customIcon = new CustomIconBuilder().uuid(iconUuid).data(customPng).build();
		CustomIcons customIcons = new CustomIconsBuilder().addIcon(customIcon).build();

		Meta meta = new MetaBuilder("iconTest").customIcons(customIcons).build();
		Entry entry1 = new EntryBuilder("1").customIconUuid(iconUuid).build();
		Group groupA = new GroupBuilder("A").customIconUuid(iconUuid).addEntry(entry1).build();

		KeePassFile keePassFile = new KeePassFileBuilder(meta).addTopGroups(groupA).build();

		// write
		String dbFilename = "target/test-classes/databaseWithCustomIcon.kdbx";
		KeePassDatabase.write(keePassFile, "abcdefg", dbFilename);

		// read and assert
		KeePassFile readDb = KeePassDatabase.getInstance(dbFilename).openDatabase("abcdefg");
		Assert.assertEquals("iconTest", readDb.getMeta().getDatabaseName());
		Assert.assertEquals(iconUuid, readDb.getGroupByName("A").getCustomIconUuid());
		Assert.assertEquals(base64Icon, DatatypeConverter.printBase64Binary(readDb.getGroupByName("A").getIconData()));
		Assert.assertEquals(iconUuid, readDb.getEntryByTitle("1").getCustomIconUuid());
		Assert.assertEquals(base64Icon, DatatypeConverter.printBase64Binary(readDb.getEntryByTitle("1").getIconData()));
	}
}
