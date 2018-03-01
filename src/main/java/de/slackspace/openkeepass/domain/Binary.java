package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import de.slackspace.openkeepass.exception.AttachmentUnreadableException;
import de.slackspace.openkeepass.exception.AttachmentUnwriteableException;
import de.slackspace.openkeepass.util.StreamUtils;

/**
 * Represents a binary item in the KeePass database.
 *
 */
@Root(strict = false, name = "Binary")
public class Binary {

    @Attribute(name = "ID")
    private int id;

    @Attribute(name = "Compressed")
    private Boolean isCompressed;

    @Text
    private byte[] data;

    Binary() {}

    public Binary(int id, byte[] data) {
        this(id, data, true);
    }

    public Binary(int id, byte[] data, boolean isCompressed) {
        this.id = id;
        this.isCompressed = isCompressed;
        setData(data, isCompressed);
    }

    public Binary(BinaryContract binaryContract) {
        this.id = binaryContract.getId();
        this.isCompressed = binaryContract.isCompressed();

        if (isCompressed) {
            this.data = compressData(binaryContract.getData());
        }
        else {
            this.data = binaryContract.getData();
        }
    }

    /**
     * Returns the id of this binary.
     *
     * @return the id of the binary
     */
    public int getId() {
        return id;
    }

    public Binary setId(int id) {
        this.id = id;

        return this;
    }

    /**
     * Returns whether this binary is compressed.
     *
     * @return true if the binary is compressed
     */
    public boolean isCompressed() {
        if (isCompressed == null) {
            return false;
        }
        return isCompressed;
    }

    /**
     * Returns the raw binary data as bytes.
     *
     * @return raw binary data as bytes
     */
    public byte[] getData() {
        if (isCompressed()) {
            return decompressData();
        }

        return data;
    }

    public Binary setData(byte[] data, boolean isCompressed) {
        if (isCompressed) {
            this.data = compressData(data);
        }
        else {
            this.data = data;
        }

        return this;
    }

    private byte[] decompressData() {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data))) {
            byte[] decompressed = StreamUtils.toByteArray(gzipInputStream);
            return decompressed;
        }
        catch (IOException e) {
            throw new AttachmentUnreadableException("Could not read attachment from resource with id '" + id + "'", e);
        }
    }

    private byte[] compressData(byte[] data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream streamToZip = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(streamToZip);) {
            gzipOutputStream.write(data);

            // this is necessary even if auto-close is in place!
            gzipOutputStream.close();
            return streamToZip.toByteArray();
        }
        catch (IOException e) {
            throw new AttachmentUnwriteableException(
                    "Could not compress attachment with id '" + this.id + "'");
        }
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + id;
        result = prime * result + ((isCompressed == null) ? 0 : isCompressed.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Binary)) {
            return false;
        }
        Binary other = (Binary) obj;
        if (id != other.id) {
            return false;
        }
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        if (isCompressed == null) {
            if (other.isCompressed != null) {
                return false;
            }
        }
        else if (!isCompressed.equals(other.isCompressed)) {
            return false;
        }
        return true;
    }
}