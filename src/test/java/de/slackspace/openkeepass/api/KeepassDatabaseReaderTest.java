package de.slackspace.openkeepass.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.domain.Attachment;
import de.slackspace.openkeepass.domain.CompressionAlgorithm;
import de.slackspace.openkeepass.domain.CrsAlgorithm;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.domain.Property;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.ResourceUtils;
import de.slackspace.openkeepass.util.StreamUtils;

public class KeepassDatabaseReaderTest {

    @Test
    public void whenGettingEntriesByTitleShouldReturnMatchingEntries() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("testDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Entry entry = database.getEntryByTitle("MyEntry");
        assertThat(entry.getPassword(), is("1v4QKuIUT6HHRkbq0MPL"));
    }

    @Test
    public void whenGettingModifiedEntriesByTitleShouldReturnMatchingEntries() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("testDatabaseModified.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Entry entry = database.getEntryByTitle("MyEntry");
        assertThat(entry.getPassword(), is("1v4QKuIUT6HHRkbq0MPL"));
    }

    @Test
    public void whenGettingEntriesByTitleButNothingMatchesShouldReturnNull() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("testDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Entry entry = database.getEntryByTitle("abcdefgh");
        assertThat(entry, is(nullValue()));
    }

    @Test
    public void whenKeePassFileIsV2ShouldReadHeader() throws IOException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("testDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassHeader header = reader.getHeader();

        assertThat(ByteUtils.hexStringToByteArray("31C1F2E6BF714350BE5805216AFC5AFF"), is(header.getCipher()));
        assertThat(header.getCompression(), is(CompressionAlgorithm.Gzip));
        assertThat(header.getTransformRounds(), is(8000L));
        assertThat(header.getEncryptionIV(), is(ByteUtils.hexStringToByteArray("2c605455f181fbc9462aefb817852b37")));
        assertThat(header.getStreamStartBytes(), is(ByteUtils.hexStringToByteArray("69d788d9b01ea1facd1c0bf0187e7d74e4aa07b20d464f3d23d0b2dc2f059ff8")));
        assertThat(header.getCrsAlgorithm(), is(CrsAlgorithm.Salsa20));
        assertThat(header.getMasterSeed(), is(ByteUtils.hexStringToByteArray("35ac8b529bc4f6e44194bccd0537fcb433a30bcb847e63156262c4df99c528ca")));
        assertThat(header.getTransformSeed(), is(ByteUtils.hexStringToByteArray("0d52d93efc5493ae6623f0d5d69bb76bd976bb717f4ee67abbe43528ebfbb646")));
        assertThat(header.getProtectedStreamKey(), is(ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257")));
        assertThat(header.getHeaderSize(), is(210));
    }

    @Test
    public void whenPasswordIsValidShouldOpenKeepassFile() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("testDatabase.kdbx"));
        KeePassDatabase reader = KeePassDatabase.getInstance(file);

        KeePassFile database = reader.openDatabase("abcdefg");
        assertThat(database, is(notNullValue()));
        assertThat(database.getMeta().getDatabaseName(), is("TestDatabase"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenKeePassFileIsOldShouldThrowException() {
        byte[] header = ByteUtils.hexStringToByteArray("03d9a29a65fb4bb5");

        ByteArrayInputStream file = new ByteArrayInputStream(header, header.length, 0);
        KeePassDatabase.getInstance(file);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenNotAKeePassFileShouldThrowException() {
        byte[] header = ByteUtils.hexStringToByteArray("0011223344556677");

        ByteArrayInputStream file = new ByteArrayInputStream(header, header.length, 0);
        KeePassDatabase.getInstance(file);
    }

    @Test
    public void testIfPasswordsCanBeDecrypted() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("fullBlownDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("123456");

        List<Entry> entries = database.getEntries();

        assertThat(entries.get(0).getPassword(), is("2f29047129b9e4c48f05d09907e52b9b"));
        assertThat(entries.get(1).getPassword(), is("GzteT206M4bVvHYaKPpA"));
        assertThat(entries.get(2).getPassword(), is("gC03cizrzcBxytfKurWQ"));
        assertThat(entries.get(3).getPassword(), is("jXjHEh3c8wcl0hank0qG"));
        assertThat(entries.get(4).getPassword(), is("wkzB5KGIUoP8LKSSEngX"));
    }

    @Test
    public void whenEntryHasCustomPropertiesShouldReadCustomProperties() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("fullBlownDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("123456");

        Entry entry = database.getEntryByTitle("6th Entry");
        assertThat(entry.getTitle(), is("6th Entry"));

        Property customProperty = entry.getPropertyByName("x");
        assertThat(customProperty, is(notNullValue()));
        assertThat(customProperty.getValue(), is("y"));
    }

    @Test
    public void whenPasswordOfEntryIsEmptyShouldReturnEmptyValue() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("DatabaseWithEmptyPassword.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("1234");

        Entry entryWithEmptyPassword = database.getEntryByTitle("EntryWithEmptyPassword");
        assertThat(entryWithEmptyPassword.getUsername(), is("UsernameNotEmpty"));
        assertThat(entryWithEmptyPassword.getPassword(), is(""));

        Entry entryWithEmptyUsername = database.getEntryByTitle("EntryWithEmptyUsername");
        assertThat(entryWithEmptyUsername.getUsername(), is(""));
        assertThat(entryWithEmptyUsername.getPassword(), is("1234"));

        Entry entryWithEmptyUserAndPassword = database.getEntryByTitle("EmptyEntry");
        assertThat(entryWithEmptyUserAndPassword.getUsername(), is(""));
        assertThat(entryWithEmptyUserAndPassword.getPassword(), is(""));
    }

    @Test
    public void whenKeePassFileIsSecuredWithBinaryKeyFileShouldOpenKeePassFileWithKeyFile() throws FileNotFoundException {
        FileInputStream keePassFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithBinaryKeyfile.kdbx"));
        FileInputStream keyFile = new FileInputStream(ResourceUtils.getResource("0.png"));

        KeePassFile database = KeePassDatabase.getInstance(keePassFile).openDatabase(keyFile);

        List<Entry> entries = database.getEntries();
        assertThat(entries.get(0).getPassword(), is("1234567"));
    }

    @Test
    public void whenKeePassFileIsSecuredWithBinaryKeyFileAndPasswordShouldOpenKeePassFile() throws FileNotFoundException {
        FileInputStream keePassFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithPasswordAndBinaryKeyfile.kdbx"));
        FileInputStream keyFile = new FileInputStream(ResourceUtils.getResource("0.png"));

        KeePassFile database = KeePassDatabase.getInstance(keePassFile).openDatabase("1234", keyFile);

        List<Entry> entries = database.getEntries();
        assertThat(entries.get(0).getPassword(), is("qwerty"));
    }

    @Test
    public void whenKeePassFileIsSecuredWithKeyFileShouldOpenKeePassFileWithKeyFile() throws FileNotFoundException {
        FileInputStream keePassFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithKeyfile.kdbx"));
        FileInputStream keyFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithKeyfile.key"));

        KeePassFile database = KeePassDatabase.getInstance(keePassFile).openDatabase(keyFile);

        List<Entry> entries = database.getEntries();
        assertThat(entries.get(0).getPassword(), is("V6uoqOm7esGRqm20VvMz"));
    }

    @Test
    public void whenKeePassFileIsSecuredWithPasswordAndKeyFileShouldOpenKeePassFileWithPasswordAndKeyFile() throws FileNotFoundException {
        FileInputStream keePassFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithPasswordAndKeyfile.kdbx"));
        FileInputStream keyFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithPasswordAndKeyfile.key"));

        KeePassFile database = KeePassDatabase.getInstance(keePassFile).openDatabase("test123", keyFile);

        List<Entry> entries = database.getEntries();
        assertThat(entries.get(0).getPassword(), is("V6uoqOm7esGRqm20VvMz"));
    }

    @Test(expected = KeePassDatabaseUnreadableException.class)
    public void whenKeePassFileIsSecuredWithPasswordAndKeyFileShouldNotOpenKeePassFileWithPassword() throws FileNotFoundException {
        FileInputStream keePassFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithPasswordAndKeyfile.kdbx"));

        KeePassDatabase.getInstance(keePassFile).openDatabase("test123");
    }

    @Test(expected = KeePassDatabaseUnreadableException.class)
    public void whenKeePassFileIsSecuredWithPasswordAndKeyFileShouldNotOpenKeePassFileWithKeyFile() throws FileNotFoundException {
        FileInputStream keePassFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithPasswordAndKeyfile.kdbx"));
        FileInputStream keyFile = new FileInputStream(ResourceUtils.getResource("DatabaseWithPasswordAndKeyfile.key"));

        KeePassDatabase.getInstance(keePassFile).openDatabase(keyFile);
    }

    @Test
    public void whenGettingInstanceByStringShouldOpenDatabase() throws FileNotFoundException {
        KeePassFile database = KeePassDatabase.getInstance(ResourceUtils.getResource("fullBlownDatabase.kdbx")).openDatabase("123456");
        List<Entry> entries = database.getEntries();
        assertThat(entries.get(0).getPassword(), is("2f29047129b9e4c48f05d09907e52b9b"));
    }

    @Test
    public void whenGettingInstanceByFileShouldOpenDatabase() {
        KeePassFile database = KeePassDatabase.getInstance(new File(ResourceUtils.getResource("fullBlownDatabase.kdbx"))).openDatabase("123456");
        List<Entry> entries = database.getEntries();
        assertThat(entries.get(0).getPassword(), is("2f29047129b9e4c48f05d09907e52b9b"));
    }

    @Test
    public void whenGettingEntriesFromKeeFoxShouldDecryptEntries() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("KeeFoxDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcd1234");

        List<Entry> entries = database.getEntries();
        assertThat(entries.get(0).getTitle(), is("Sample Entry"));
        assertThat(entries.get(0).getPassword(), is("Password"));

        assertThat(entries.get(1).getTitle(), is("Sample Entry #2"));
        assertThat(entries.get(1).getPassword(), is("12345"));

        assertThat(entries.get(2).getTitle(), is("Sign in - Google Accounts"));
        assertThat(entries.get(2).getPassword(), is("test"));
    }

    @Test
    public void whenGettingEntryByUUIDShouldReturnFoundEntry() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("testDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Entry entry = database.getEntryByUUID(uuid("1fbddfcd-52ff-1d4b-b2e8-27f671e4ea22"));
        assertThat(entry.getTitle(), is("Sample Entry #2"));
    }

    private UUID uuid(String value) {
        return UUID.fromString(value);
    }

    @Test
    public void whenGettingGroupByUUIDShouldReturnFoundGroup() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("testDatabase.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Group group = database.getGroupByUUID(uuid("16abcc27-cca3-9544-8012-df4e98d4a3d8"));
        assertThat(group.getName(), is("General"));
    }

    @Test
    public void whenGettingEntriesShouldReturnAllEntries() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("DatabaseWithComplexTree.kdbx"));

        KeePassFile db = KeePassDatabase.getInstance(file).openDatabase("MasterPassword");
        assertThat(db.getEntries().size(), is(122));
    }

    @Test
    public void whenGettingTagsShouldReturnTags() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("DatabaseWithTags.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Entry entry = database.getEntryByTitle("Sample Entry");
        List<String> tags = entry.getTags();

        assertThat(tags, hasItems("tag1", "tag2", "tag3"));
    }

    @Test
    public void whenGettingColorsShouldReturnColors() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("DatabaseWithColors.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("qwerty");

        Entry entry = database.getEntryByTitle("Sample Entry");

        assertThat(entry.getForegroundColor(), is("#0080FF"));
        assertThat(entry.getBackgroundColor(), is("#FF0000"));
    }

    @Test
    public void whenGettingAttachmentShouldReturnAttachment() throws IOException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("DatabaseWithAttachments.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Entry entry = database.getEntryByTitle("Sample Entry");

        assertThat(entry.getAttachments().size(), is(2));
        List<Attachment> attachments = entry.getAttachments();

        Attachment image = attachments.get(0);
        assertThat(image.getKey(), is("0.png"));
        assertThat(image.getRef(), is(0));

        FileInputStream originalImage = new FileInputStream(ResourceUtils.getResource("0.png"));
        byte[] originalByteArray = StreamUtils.toByteArray(originalImage);

        assertThat(image.getData(), equalTo(originalByteArray));
    }

    @Test
    public void whenGettingEntryWithReferencesShouldReturnReferencedValues() throws FileNotFoundException {
        FileInputStream file = new FileInputStream(ResourceUtils.getResource("DatabaseWithReferences.kdbx"));

        KeePassDatabase reader = KeePassDatabase.getInstance(file);
        KeePassFile database = reader.openDatabase("abcdefg");

        Entry entryA = database.getEntryByTitle("ReferenceToA");
        assertThat(entryA.getUsername(), is("testA"));
        assertThat(entryA.getPassword(), is("passwdA"));
        assertThat(entryA.getUrl(), is("http://google.com"));
        assertThat(entryA.getNotes(), is("Just a sample note"));

        Entry entryB = database.getEntryByTitle("AnotherReferenceToA");
        assertThat(entryB.getUsername(), is("passwdA"));
        assertThat(entryB.getPassword(), is("http://google.com"));
    }
}
