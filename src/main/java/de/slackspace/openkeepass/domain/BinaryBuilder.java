package de.slackspace.openkeepass.domain;

/**
 * A builder to create {@link Binary} objects.
 *
 */
public class BinaryBuilder implements BinaryContract {

    int id;

    boolean isCompressed;

    byte[] data;

    public BinaryBuilder() {
        // default no-args constructor
    }

    public BinaryBuilder(Binary binary) {
        this.id = binary.getId();
        this.isCompressed = binary.isCompressed();
        this.data = binary.getData();
    }

    public BinaryBuilder id(int id) {
        this.id = id;
        return this;
    }

    public BinaryBuilder isCompressed(boolean isCompressed) {
        this.isCompressed = isCompressed;
        return this;
    }

    public BinaryBuilder data(byte[] data) {
        this.data = data;
        return this;
    }

    /**
     * Builds a new binary with the values from the builder.
     *
     * @return a new Binary object
     */
    public Binary build() {
        return new Binary(this);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isCompressed() {
        return isCompressed;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
