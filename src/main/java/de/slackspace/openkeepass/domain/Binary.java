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

    public Binary(BinaryContract binaryContract) {
        this.id = binaryContract.getId();
        this.isCompressed = binaryContract.isCompressed();

        if (isCompressed) {
            this.data = compressData(binaryContract);
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

    private byte[] decompressData() {
        GZIPInputStream gzipInputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] decompressed = StreamUtils.toByteArray(gzipInputStream);
            return decompressed;
        }
        catch (IOException e) {
            throw new AttachmentUnreadableException("Could not read attachment from resource with id '" + id + "'", e);
        }
        finally {
            try {
                if (gzipInputStream != null) {
                    gzipInputStream.close();
                }
            }
            catch (IOException e) {
                // Ignore
            }
        }
    }

    private byte[] compressData(BinaryContract binaryContract) {
        if (binaryContract.getData() == null) {
            return null;
        }

        try {
            ByteArrayOutputStream streamToZip = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream;
            gzipOutputStream = new GZIPOutputStream(streamToZip);
            gzipOutputStream.write(binaryContract.getData());
            gzipOutputStream.close();
            return streamToZip.toByteArray();
        }
        catch (IOException e) {
            throw new AttachmentUnwriteableException(
                    "Could not compress attachment with id '" + binaryContract.getId() + "'");
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
