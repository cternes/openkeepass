package de.slackspace.openkeepass.reader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.CompressionAlgorithm;
import de.slackspace.openkeepass.domain.CrsAlgorithm;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.GroupBuilder;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnwriteable;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.xml.KeePassDatabaseXmlParser;

public class KeepassDatabaseWriterTest {

	private byte[] protectedStreamKey = ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");
	
	@Test
	public void whenWritingDatabaseFileShouldBeAbleToReadItAlso() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new KeePassDatabaseXmlParser().fromXml(fileInputStream, Salsa20.createInstance(protectedStreamKey));
		
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
		Entry entryOne = new EntryBuilder("First entry")
				.username("Carl")
				.password("Carls secret")
				.build();
		
		KeePassFile keePassFile = new KeePassFileBuilder("testDB")
				.withTopEntries(entryOne)
				.build();
		
		String dbFilename = "target/test-classes/writeNewDatabase.kdbx";
		KeePassDatabase.write(keePassFile, "abc", new FileOutputStream(dbFilename));
		
		KeePassDatabase keePassDb = KeePassDatabase.getInstance(dbFilename);
		KeePassFile database = keePassDb.openDatabase("abc");
		Entry entryByTitle = database.getEntryByTitle("First entry");
		
		Assert.assertEquals(entryOne.getTitle(), entryByTitle.getTitle());
	}
	
	@Test
	public void shouldBuildKeePassFileWithTreeStructure() throws FileNotFoundException {
		/* Should create the following structure:
		 * 
		 * Root
		 * |
		 * |-- First entry (E)
		 * |-- Banking (G)
		 * |
		 * |-- Internet (G)
		 *     |
		 *     |-- Shopping (G)
		 *     	   |-- Second entry (E)
		 *  
		 */
		Group root = new GroupBuilder()
				.addEntry(new EntryBuilder("First entry").build())
				.addGroup(new GroupBuilder("Banking").build())
				.addGroup(new GroupBuilder("Internet")
						.addGroup(new GroupBuilder("Shopping")
								.addEntry(new EntryBuilder("Second entry").build())
								.build())
						.build())
				.build();
		
		KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB")
				.withTopGroup(root)
				.build();
		
		String dbFilename = "target/test-classes/writeTreeDB.kdbx";
		KeePassDatabase.write(keePassFile, "abc", new FileOutputStream(dbFilename));
		
		KeePassDatabase keePassDb = KeePassDatabase.getInstance(dbFilename);
		KeePassFile database = keePassDb.openDatabase("abc");
		
		Assert.assertEquals("Banking", database.getTopGroups().get(0).getName());
		Assert.assertEquals("Internet", database.getTopGroups().get(1).getName());
		Assert.assertEquals("First entry", database.getTopEntries().get(0).getTitle());
		Assert.assertEquals("Shopping", database.getTopGroups().get(1).getGroups().get(0).getName());
		Assert.assertEquals("Second entry", database.getTopGroups().get(1).getGroups().get(0).getEntries().get(0).getTitle());
	}
	
	@Test(expected=KeePassDatabaseUnwriteable.class)
	public void whenRootIsNullShouldThrowExceptionOnWrite() {
		KeePassFile keePassFile = new KeePassFileBuilder("corruptDB").build();
		keePassFile.setRoot(null);
		
		KeePassDatabase.write(keePassFile, "abc", new ByteArrayOutputStream());
	}
}
