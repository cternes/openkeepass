package de.slackspace.openkeepass.domain;

import java.util.UUID;

/**
 * A builder to create {@link CustomIcon} objects.
 *
 */
public class CustomIconBuilder implements CustomIconContract {

    UUID uuid;

    byte[] data;

    public CustomIconBuilder() {
        // default no-args constructor
    }

    public CustomIconBuilder(CustomIcon customIcon) {
        this.uuid = customIcon.getUuid();
        this.data = customIcon.getData();
    }

    public CustomIconBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public CustomIconBuilder data(byte[] data) {
        this.data = data;
        return this;
    }

    /**
     * Builds a new custom icon with the values from the builder.
     *
     * @return a new CustomIcon object
     */
    public CustomIcon build() {
        return new CustomIcon(this);
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
