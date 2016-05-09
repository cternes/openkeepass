package de.slackspace.openkeepass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import de.slackspace.openkeepass.api.KeePassDatabaseReader;
import de.slackspace.openkeepass.api.KeePassDatabaseWriter;
import de.slackspace.openkeepass.api.KeyFileReader;
import de.slackspace.openkeepass.crypto.Sha256;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.StreamUtils;

/**
 * A KeePassDatabase is the central API class to read and write a KeePass
 * database file.
 * <p>
 * Currently the following KeePass files are supported:
 *
 * <ul>
 * <li>KeePass Database V2 with password</li>
 * <li>KeePass Database V2 with keyfile</li>
 * <li>KeePass Database V2 with combined password and keyfile</li>
 * </ul>
 *
 * A typical read use-case should use the following idiom:
 *
 * <pre>
 * // open database
 * KeePassFile database = KeePassDatabase.getInstance("keePassDatabasePath").openDatabase("secret");
 *
 * // get password entries
 * List&lt;Entry&gt; entries = database.getEntries();
 * ...
 * </pre>
 *
 * If the database could not be opened an exception of type
 * <tt>KeePassDatabaseUnreadable</tt> will be thrown.
 * <p>
 * A typical write use-case should use the following idiom:
 *
 * <pre>
 * // build an entry
 * Entry entryOne = new EntryBuilder("First entry").username("Carl").password("secret").build();
 *
 * // build more entries or groups as you like
 * ...
 *
 * // build KeePass model
 * KeePassFile keePassFile = new KeePassFileBuilder("testDB").addTopEntries(entryOne).build();
 *
 * // write KeePass database file
 * KeePassDatabase.write(keePassFile, "secret", new FileOutputStream("keePassDatabasePath"));
 * </pre>
 *
 * @see KeePassFile
 *
 */
public class KeePassDatabase {

    private static final String UTF_8 = "UTF-8";
    private static final String MSG_UTF8_NOT_SUPPORTED = "The encoding UTF-8 is not supported";
    private static final String MSG_EMPTY_MASTER_KEY = "The password for the database must not be null. Please provide a valid password.";

    private KeePassHeader keepassHeader = new KeePassHeader();
    private byte[] keepassFile;

    private KeePassDatabase(InputStream inputStream) {
        try {
            keepassFile = StreamUtils.toByteArray(inputStream);
            keepassHeader.checkVersionSupport(keepassFile);
            keepassHeader.read(keepassFile);
        } catch (IOException e) {
            throw new KeePassDatabaseUnreadableException("Could not open database file", e);
        }
    }

    /**
     * Retrieves a KeePassDatabase instance. The instance returned is based on
     * the given database filename and tries to parse the database header of it.
     *
     * @param keePassDatabaseFile
     *            a KeePass database filename, must not be NULL
     * @return a KeePassDatabase
     */
    public static KeePassDatabase getInstance(String keePassDatabaseFile) {
        return getInstance(new File(keePassDatabaseFile));
    }

    /**
     * Retrieves a KeePassDatabase instance. The instance returned is based on
     * the given database file and tries to parse the database header of it.
     *
     * @param keePassDatabaseFile
     *            a KeePass database file, must not be NULL
     * @return a KeePassDatabase
     */
    public static KeePassDatabase getInstance(File keePassDatabaseFile) {
        if (keePassDatabaseFile == null) {
            throw new IllegalArgumentException("You must provide a valid KeePass database file.");
        }

        InputStream keePassDatabaseStream = null;
        try {
            keePassDatabaseStream = new FileInputStream(keePassDatabaseFile);
            return getInstance(keePassDatabaseStream);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The KeePass database file could not be found. You must provide a valid KeePass database file.", e);
        } finally {
            if (keePassDatabaseStream != null) {
                try {
                    keePassDatabaseStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Retrieves a KeePassDatabase instance. The instance returned is based on
     * the given input stream and tries to parse the database header of it.
     *
     * @param keePassDatabaseStream
     *            an input stream of a KeePass database, must not be NULL
     * @return a KeePassDatabase
     */
    public static KeePassDatabase getInstance(InputStream keePassDatabaseStream) {
        if (keePassDatabaseStream == null) {
            throw new IllegalArgumentException("You must provide a non-empty KeePass database stream.");
        }

        return new KeePassDatabase(keePassDatabaseStream);
    }

    /**
     * Opens a KeePass database with the given password and returns the
     * KeePassFile for further processing.
     * <p>
     * If the database cannot be decrypted with the provided password an
     * exception will be thrown.
     *
     * @param password
     *            the password to open the database
     * @return a KeePassFile
     * @see KeePassFile
     */
    public KeePassFile openDatabase(String password) {
        if (password == null) {
            throw new IllegalArgumentException(MSG_EMPTY_MASTER_KEY);
        }

        try {
            byte[] passwordBytes = password.getBytes(UTF_8);
            byte[] hashedPassword = Sha256.hash(passwordBytes);

            return new KeePassDatabaseReader(keepassHeader).decryptAndParseDatabase(hashedPassword, keepassFile);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(MSG_UTF8_NOT_SUPPORTED, e);
        }
    }

    /**
     * Opens a KeePass database with the given password and keyfile and returns
     * the KeePassFile for further processing.
     * <p>
     * If the database cannot be decrypted with the provided password and
     * keyfile an exception will be thrown.
     *
     * @param password
     *            the password to open the database
     * @param keyFile
     *            the password to open the database
     * @return a KeePassFile
     * @see KeePassFile
     */
    public KeePassFile openDatabase(String password, File keyFile) {
        if (password == null) {
            throw new IllegalArgumentException(MSG_EMPTY_MASTER_KEY);
        }
        if (keyFile == null) {
            throw new IllegalArgumentException("You must provide a valid KeePass keyfile.");
        }

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(keyFile);
            return openDatabase(password, inputStream);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The KeePass keyfile could not be found. You must provide a valid KeePass keyfile.", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Opens a KeePass database with the given password and keyfile stream and
     * returns the KeePassFile for further processing.
     * <p>
     * If the database cannot be decrypted with the provided password and
     * keyfile stream an exception will be thrown.
     *
     * @param password
     *            the password to open the database
     * @param keyFileStream
     *            the keyfile to open the database as stream
     * @return a KeePassFile
     * @see KeePassFile
     */
    public KeePassFile openDatabase(String password, InputStream keyFileStream) {
        if (password == null) {
            throw new IllegalArgumentException(MSG_EMPTY_MASTER_KEY);
        }
        if (keyFileStream == null) {
            throw new IllegalArgumentException("You must provide a non-empty KeePass keyfile stream.");
        }

        try {
            byte[] passwordBytes = password.getBytes(UTF_8);
            byte[] hashedPassword = Sha256.hash(passwordBytes);
            byte[] protectedBuffer = new KeyFileReader().readKeyFile(keyFileStream);

            return new KeePassDatabaseReader(keepassHeader).decryptAndParseDatabase(ByteUtils.concat(hashedPassword, protectedBuffer), keepassFile);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(MSG_UTF8_NOT_SUPPORTED, e);
        }
    }

    /**
     * Opens a KeePass database with the given keyfile and returns the
     * KeePassFile for further processing.
     * <p>
     * If the database cannot be decrypted with the provided password an
     * exception will be thrown.
     *
     * @param keyFile
     *            the password to open the database
     * @return a KeePassFile the keyfile to open the database
     * @see KeePassFile
     */
    public KeePassFile openDatabase(File keyFile) {
        if (keyFile == null) {
            throw new IllegalArgumentException("You must provide a valid KeePass keyfile.");
        }

        try {
            return openDatabase(new FileInputStream(keyFile));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The KeePass keyfile could not be found. You must provide a valid KeePass keyfile.", e);
        }
    }

    /**
     * Opens a KeePass database with the given keyfile stream and returns the
     * KeePassFile for further processing.
     * <p>
     * If the database cannot be decrypted with the provided keyfile an
     * exception will be thrown.
     *
     * @param keyFileStream
     *            the keyfile to open the database as stream
     * @return a KeePassFile
     * @see KeePassFile
     */
    public KeePassFile openDatabase(InputStream keyFileStream) {
        if (keyFileStream == null) {
            throw new IllegalArgumentException("You must provide a non-empty KeePass keyfile stream.");
        }

        byte[] protectedBuffer = new KeyFileReader().readKeyFile(keyFileStream);
        return new KeePassDatabaseReader(keepassHeader).decryptAndParseDatabase(protectedBuffer, keepassFile);
    }

    /**
     * Gets the KeePassDatabase header.
     *
     * @return the database header
     */
    public KeePassHeader getHeader() {
        return keepassHeader;
    }

    /**
     * Encrypts a {@link KeePassFile} with the given password and writes it to
     * the given file location.
     * <p>
     * If the KeePassFile cannot be encrypted an exception will be thrown.
     *
     * @param keePassFile
     *            the keePass model which should be written
     * @param password
     *            the password to encrypt the database
     * @param keePassDatabaseFile
     *            the target location where the database file will be written
     * @see KeePassFile
     */
    public static void write(KeePassFile keePassFile, String password, String keePassDatabaseFile) {
        if (keePassDatabaseFile == null || keePassDatabaseFile.isEmpty()) {
            throw new IllegalArgumentException("You must provide a non-empty path where the database should be written to.");
        }

        try {
            write(keePassFile, password, new FileOutputStream(keePassDatabaseFile));
        } catch (FileNotFoundException e) {
            throw new KeePassDatabaseUnreadableException("Could not find database file", e);
        }
    }

    /**
     * Encrypts a {@link KeePassFile} with the given password and writes it to
     * the given stream.
     * <p>
     * If the KeePassFile cannot be encrypted an exception will be thrown.
     *
     * @param keePassFile
     *            the keePass model which should be written
     * @param password
     *            the password to encrypt the database
     * @param stream
     *            the target stream where the output will be written
     * @see KeePassFile
     *
     */
    public static void write(KeePassFile keePassFile, String password, OutputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("You must provide a stream to write to.");
        }

        new KeePassDatabaseWriter().writeKeePassFile(keePassFile, password, stream);
    }

}
