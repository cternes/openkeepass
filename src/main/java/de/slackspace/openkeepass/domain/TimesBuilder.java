package de.slackspace.openkeepass.domain;

import java.util.Calendar;

/**
 * A builder to create {@link Times} objects.
 *
 */
public class TimesBuilder implements TimesContract {

    private Calendar lastModificationTime;

    private Calendar creationTime;

    private Calendar lastAccessTime;

    private Calendar expiryTime;

    private boolean expires;

    private int usageCount;

    private Calendar locationChanged;

    public TimesBuilder() {
        // default no-args constructor
    }

    public TimesBuilder(Times times) {
        this.lastModificationTime = times.getLastModificationTime();
        this.creationTime = times.getCreationTime();
        this.lastAccessTime = times.getLastAccessTime();
        this.expiryTime = times.getExpiryTime();
        this.expires = times.expires();
        this.usageCount = times.getUsageCount();
        this.locationChanged = times.getLocationChanged();
    }

    public TimesBuilder lastModificationTime(Calendar lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
        return this;
    }

    public TimesBuilder creationTime(Calendar creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public TimesBuilder lastAccessTime(Calendar lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    public TimesBuilder expiryTime(Calendar expiryTime) {
        this.expiryTime = expiryTime;
        return this;
    }

    public TimesBuilder expires(boolean expires) {
        this.expires = expires;
        return this;
    }

    public TimesBuilder usageCount(int usageCount) {
        this.usageCount = usageCount;
        return this;
    }

    public TimesBuilder locationChanged(Calendar locationChanged) {
        this.locationChanged = locationChanged;
        return this;
    }

    /**
     * Builds a new times with the values from the builder.
     *
     * @return a new times object
     */
    public Times build() {
        return new Times(this);
    }

    @Override
    public Calendar getLastModificationTime() {
        return lastModificationTime;
    }

    @Override
    public Calendar getCreationTime() {
        return creationTime;
    }

    @Override
    public Calendar getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public Calendar getExpiryTime() {
        return expiryTime;
    }

    @Override
    public boolean getExpires() {
        return expires;
    }

    @Override
    public int getUsageCount() {
        return usageCount;
    }

    @Override
    public Calendar getLocationChanged() {
        return locationChanged;
    }
}
