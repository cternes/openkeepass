package de.slackspace.openkeepass.api;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.Attachment;
import de.slackspace.openkeepass.domain.Binaries;
import de.slackspace.openkeepass.domain.BinariesBuilder;
import de.slackspace.openkeepass.domain.Binary;
import de.slackspace.openkeepass.domain.BinaryBuilder;
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
import de.slackspace.openkeepass.domain.Times;
import de.slackspace.openkeepass.domain.TimesBuilder;
import de.slackspace.openkeepass.domain.zipper.GroupZipper;
import de.slackspace.openkeepass.parser.KeePassDatabaseXmlParser;
import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.DecryptionStrategy;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.CalendarHandler;
import de.slackspace.openkeepass.util.ResourceUtils;
import de.slackspace.openkeepass.util.StreamUtils;

public class KeepassDatabaseWriterTest {

    private byte[] protectedStreamKey =
            ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void whenWritingDatabaseFileShouldBeAbleToReadItAlso() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getResource("testDatabase_decrypted.xml"));
        DecryptionStrategy strategy = new DecryptionStrategy(Salsa20.createInstance(protectedStreamKey));
        KeePassFile keePassFile =
                new KeePassDatabaseXmlParser(new SimpleXmlParser()).fromXml(fileInputStream, strategy);

        String writeDatabase = tempFolder.newFile("writeDatabase.kdbx").getPath();

        FileOutputStream file = new FileOutputStream(writeDatabase);
        KeePassDatabase.write(keePassFile, "abcdefg", file);

        KeePassDatabase database = KeePassDatabase.getInstance(writeDatabase);
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
    public void shouldCreateNewDatabaseFile() throws IOException {
        Calendar creationDate = CalendarHandler.createCalendar(2016, 5, 1);
        Times times = new TimesBuilder().usageCount(5).creationTime(creationDate).build();
        Entry entryOne = new EntryBuilder("First entry").username("Carl").password("Carls secret").times(times).build();

        KeePassFile keePassFile = new KeePassFileBuilder("testDB").addTopEntries(entryOne).build();

        String dbFilename = tempFolder.newFile("writeNewDatabase.kdbx").getPath();
        KeePassDatabase.write(keePassFile, "abc", new FileOutputStream(dbFilename));

        KeePassDatabase keePassDb = KeePassDatabase.getInstance(dbFilename);
        KeePassFile database = keePassDb.openDatabase("abc");
        Entry entryByTitle = database.getEntryByTitle("First entry");

        Assert.assertEquals(entryOne.getTitle(), entryByTitle.getTitle());
        Assert.assertEquals(5, entryOne.getTimes().getUsageCount());
        Assert.assertEquals(creationDate, entryOne.getTimes().getCreationTime());
    }

    @Test
    public void shouldBuildKeePassFileWithTreeStructure() throws IOException {
        /*
         * Should create the following structure:
         *
         * Root | |-- First entry (E) |-- Banking (G) | |-- Internet (G) | |-- Shopping (G) |-- Second entry (E)
         *
         */
        Group root = new GroupBuilder().addEntry(new EntryBuilder("First entry").build())
                .addGroup(new GroupBuilder("Banking").build())
                .addGroup(
                        new GroupBuilder("Internet").addGroup(
                                new GroupBuilder("Shopping").addEntry(new EntryBuilder("Second entry").build()).build())
                                .build())
                .build();

        KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB").addTopGroups(root).build();

        String dbFilename = tempFolder.newFile("writeTreeDB.kdbx").getPath();
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
    public void shouldModifiyGroupInKeePassFile() throws IOException {
        String password = "123456";
        KeePassDatabase keePassDb = KeePassDatabase.getInstance(ResourceUtils.getResource("fullBlownDatabase.kdbx"));
        KeePassFile database = keePassDb.openDatabase(password);

        Group group = database.getGroupByName("test");
        Group modifiedGroup = new GroupBuilder(group).name("test2").build();

        GroupZipper zipper = new GroupZipper(database).down().right().right().right().right().down();
        KeePassFile modifiedDatabase = zipper.replace(modifiedGroup).close();

        String dbFilename = tempFolder.newFile("fullBlownDatabaseModified.kdbx").getPath();
        KeePassDatabase.write(modifiedDatabase, password, new FileOutputStream(dbFilename));
        KeePassFile databaseReadFromHdd = KeePassDatabase.getInstance(dbFilename).openDatabase(password);

        Assert.assertNotNull("Banking", databaseReadFromHdd.getGroupByName("test2"));
        Assert.assertEquals(2, databaseReadFromHdd.getGroupByName("test2").getEntries().size());
    }

    @Test
    public void shouldModifyMetadataAndRenameGeneralNodeThenWriteAndReadDatabase() throws IOException {
        String password = "abcdefg";
        String originalDbFile = ResourceUtils.getResource("testDatabase.kdbx");
        String modifiedDbFile = tempFolder.newFile("modifiedtestDatabase2.kdbx").getPath();

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
    public void shouldWriteDatabaseWithCustomIcon() throws IOException {
        String base64Icon =
                "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAMAAADXqc3KAAAB+FBMVEUAAAA/mUPidDHiLi5Cn0XkNTPmeUrkdUg/m0Q0pEfcpSbwaVdKskg+lUP4zA/iLi3msSHkOjVAmETdJSjtYFE/lkPnRj3sWUs8kkLeqCVIq0fxvhXqUkbVmSjwa1n1yBLepyX1xxP0xRXqUkboST9KukpHpUbuvRrzrhF/ljbwaljuZFM4jELaoSdLtElJrUj1xxP6zwzfqSU4i0HYnydMtUlIqUfywxb60AxZqEXaoifgMCXptR9MtklHpEY2iUHWnSjvvRr70QujkC+pUC/90glMuEnlOjVMt0j70QriLS1LtEnnRj3qUUXfIidOjsxAhcZFo0bjNDH0xxNLr0dIrUdmntVTkMoyfL8jcLBRuErhJyrgKyb4zA/5zg3tYFBBmUTmQTnhMinruBzvvhnxwxZ/st+Ktt5zp9hqota2vtK6y9FemNBblc9HiMiTtMbFtsM6gcPV2r6dwroseLrMrbQrdLGdyKoobKbo3Zh+ynrgVllZulTsXE3rV0pIqUf42UVUo0JyjEHoS0HmsiHRGR/lmRz/1hjqnxjvpRWfwtOhusaz0LRGf7FEfbDVmqHXlJeW0pbXq5bec3fX0nTnzmuJuWvhoFFhm0FtrziBsjaAaDCYWC+uSi6jQS3FsSfLJiTirCOkuCG1KiG+wSC+GBvgyhTszQ64Z77KAAAARXRSTlMAIQRDLyUgCwsE6ebm5ubg2dLR0byXl4FDQzU1NDEuLSUgC+vr6urq6ubb29vb2tra2tG8vLu7u7uXl5eXgYGBgYGBLiUALabIAAABsElEQVQoz12S9VPjQBxHt8VaOA6HE+AOzv1wd7pJk5I2adpCC7RUcHd3d3fXf5PvLkxheD++z+yb7GSRlwD/+Hj/APQCZWxM5M+goF+RMbHK594v+tPoiN1uHxkt+xzt9+R9wnRTZZQpXQ0T5uP1IQxToyOAZiQu5HEpjeA4SWIoksRxNiGC1tRZJ4LNxgHgnU5nJZBDvuDdl8lzQRBsQ+s9PZt7s7Pz8wsL39/DkIfZ4xlB2Gqsq62ta9oxVlVrNZpihFRpGO9fzQw1ms0NDWZz07iGkJmIFH8xxkc3a/WWlubmFkv9AB2SEpDvKxbjidN2faseaNV3zoHXvv7wMODJdkOHAegweAfFPx4G67KluxzottCU9n8CUqXzcIQdXOytAHqXxomvykhEKN9EFutG22p//0rbNvHVxiJywa8yS2KDfV1dfbu31H8jF1RHiTKtWYeHxUvq3bn0pyjCRaiRU6aDO+gb3aEfEeVNsDgm8zzLy9egPa7Qt8TSJdwhjplk06HH43ZNJ3s91KKCHQ5x4sw1fRGYDZ0n1L4FKb9/BP5JLYxToheoFCVxz57PPS8UhhEpLBVeAAAAAElFTkSuQmCC";

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
        String dbFilename = tempFolder.newFile("databaseWithCustomIcon.kdbx").getPath();
        KeePassDatabase.write(keePassFile, "abcdefg", dbFilename);

        // read and assert
        KeePassFile readDb = KeePassDatabase.getInstance(dbFilename).openDatabase("abcdefg");
        Assert.assertEquals("iconTest", readDb.getMeta().getDatabaseName());
        Assert.assertEquals(iconUuid, readDb.getGroupByName("A").getCustomIconUuid());
        Assert.assertEquals(base64Icon, DatatypeConverter.printBase64Binary(readDb.getGroupByName("A").getIconData()));
        Assert.assertEquals(iconUuid, readDb.getEntryByTitle("1").getCustomIconUuid());
        Assert.assertEquals(base64Icon, DatatypeConverter.printBase64Binary(readDb.getEntryByTitle("1").getIconData()));
    }

    @Test
    public void shouldWriteDatabaseWithAttachment() throws IOException {
        FileInputStream image = new FileInputStream(ResourceUtils.getResource("0.png"));
        byte[] imageData = StreamUtils.toByteArray(image);

        String attachmentKey = "dummy.png";
        int attachmentId = 0;

        Binary binary = new BinaryBuilder().id(attachmentId).isCompressed(true).data(imageData).build();
        Binaries binaries = new BinariesBuilder().addBinary(binary).build();

        Meta meta = new MetaBuilder("attachmentTest").binaries(binaries).build();
        Entry entry = new EntryBuilder("1").addAttachment(attachmentKey, attachmentId).build();
        Group group = new GroupBuilder("A").addEntry(entry).build();

        KeePassFile keePassFile = new KeePassFileBuilder(meta).addTopGroups(group).build();

        // write
        String dbFilename = tempFolder.newFile("databaseWithAttachment.kdbx").getPath();
        KeePassDatabase.write(keePassFile, "abcdefg", dbFilename);

        // read and assert
        KeePassFile readDb = KeePassDatabase.getInstance(dbFilename).openDatabase("abcdefg");
        Assert.assertEquals("attachmentTest", readDb.getMeta().getDatabaseName());
        List<Attachment> attachments = readDb.getEntryByTitle("1").getAttachments();

        assertThat(attachments.size(), is(1));
        assertThat(attachments.get(0).getRef(), is(attachmentId));
        assertThat(attachments.get(0).getKey(), is(attachmentKey));
        assertThat(attachments.get(0).getData(), is(imageData));
    }

    @Test
    public void shouldEnsureThatEntriesAreNotModifiedDuringWriting() throws IOException {
        // open DB
        final KeePassFile keePassFile =
                KeePassDatabase.getInstance(ResourceUtils.getResource("testDatabase.kdbx")).openDatabase("abcdefg");
        Group generalGroup = keePassFile.getGroupByName("General");

        // add entry
        Entry entry = new EntryBuilder(UUID.randomUUID()).title("title").password("password").build();
        new GroupBuilder(generalGroup).addEntry(entry).build();

        // compare password in current DB
        Assert.assertEquals(entry.getPassword(), generalGroup.getEntryByTitle("title").getPassword());

        // get origin passwords
        List<String> originPasswords = new ArrayList<String>();
        List<Entry> entries = keePassFile.getEntries();
        for (Entry e : entries) {
            originPasswords.add(e.getPassword());
        }

        // write to new DB
        KeePassDatabase.write(keePassFile, "abcdefg", tempFolder.newFile("testDatabase_new.kdbx").getPath());

        // get final passwords
        List<String> finalPasswords = new ArrayList<String>();
        for (Entry e : entries) {
            finalPasswords.add(e.getPassword());
        }

        // compare original passwords with final ones
        assertThat(originPasswords, is(finalPasswords));
    }

    @Test
    public void shouldWriteDatabaseWithTags() throws IOException {
        // build database
        Meta meta = new MetaBuilder("tagTest").build();
        Entry entry1 = new EntryBuilder("1").addTag("x").addTag("y").build();
        Group groupA = new GroupBuilder("A").addEntry(entry1).build();

        KeePassFile keePassFile = new KeePassFileBuilder(meta).addTopGroups(groupA).build();

        // write
        String dbFilename = tempFolder.newFile("dbWithTags.kdbx").getPath();
        KeePassDatabase.write(keePassFile, "abcdefg", dbFilename);

        // read and assert
        KeePassFile readDb = KeePassDatabase.getInstance(dbFilename).openDatabase("abcdefg");
        assertThat(readDb.getMeta().getDatabaseName(), is("tagTest"));

        List<String> tags = readDb.getEntryByTitle("1").getTags();
        assertThat(tags, hasItems("x", "y"));
    }

    @Test
    public void shouldWriteDatabaseWithColors() throws IOException {
        // build database
        Meta meta = new MetaBuilder("colorTest").build();
        Entry entry1 = new EntryBuilder("1").foregroundColor("#FFFFFF").backgroundColor("#000000").build();
        Group groupA = new GroupBuilder("A").addEntry(entry1).build();

        KeePassFile keePassFile = new KeePassFileBuilder(meta).addTopGroups(groupA).build();

        // write
        String dbFilename = tempFolder.newFile("dbWithColors.kdbx").getPath();
        KeePassDatabase.write(keePassFile, "abcdefg", dbFilename);

        // read and assert
        KeePassFile readDb = KeePassDatabase.getInstance(dbFilename).openDatabase("abcdefg");
        assertThat(readDb.getMeta().getDatabaseName(), is("colorTest"));

        Entry entry = readDb.getEntryByTitle("1");
        Assert.assertEquals("#FFFFFF", entry.getForegroundColor());
        Assert.assertEquals("#000000", entry.getBackgroundColor());
    }

}
