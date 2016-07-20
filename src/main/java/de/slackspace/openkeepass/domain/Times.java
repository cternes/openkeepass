package de.slackspace.openkeepass.domain;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents statistical information of an {@link Entry}.
 *
 */
@Root(strict = false)
public class Times {

    @Element(name = "LastModificationTime", type = GregorianCalendar.class, required = false)
    private Calendar lastModificationTime;

    @Element(name = "CreationTime", type = GregorianCalendar.class, required = false)
    private Calendar creationTime;

    @Element(name = "LastAccessTime", type = GregorianCalendar.class, required = false)
    private Calendar lastAccessTime;

    @Element(name = "ExpiryTime", type = GregorianCalendar.class, required = false)
    private Calendar expiryTime;

    @Element(name = "Expires", required = false)
    private Boolean expires;

    @Element(name = "UsageCount", required = false)
    private int usageCount;

    @Element(name = "LocationChanged", type = GregorianCalendar.class, required = false)
    private Calendar locationChanged;

    Times() {
        // default no-args constructor
    }

    public Times(TimesContract timesContract) {
        this.creationTime = timesContract.getCreationTime();
        this.expires = timesContract.getExpires();
        this.expiryTime = timesContract.getExpiryTime();
        this.lastAccessTime = timesContract.getLastAccessTime();
        this.lastModificationTime = timesContract.getLastModificationTime();
        this.locationChanged = timesContract.getLocationChanged();
        this.usageCount = timesContract.getUsageCount();
    }

    public Calendar getLastModificationTime() {
        return lastModificationTime;
    }

    public Calendar getCreationTime() {
        return creationTime;
    }

    public Calendar getLastAccessTime() {
        return lastAccessTime;
    }

    public Calendar getExpiryTime() {
        return expiryTime;
    }

    public boolean expires() {
        return expires;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public Calendar getLocationChanged() {
        return locationChanged;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
        result = prime * result + ((expires == null) ? 0 : expires.hashCode());
        result = prime * result + ((expiryTime == null) ? 0 : expiryTime.hashCode());
        result = prime * result + ((lastAccessTime == null) ? 0 : lastAccessTime.hashCode());
        result = prime * result + ((lastModificationTime == null) ? 0 : lastModificationTime.hashCode());
        result = prime * result + ((locationChanged == null) ? 0 : locationChanged.hashCode());
        result = prime * result + usageCount;
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Times))
            return false;
        Times other = (Times) obj;
        if (creationTime == null) {
            if (other.creationTime != null)
                return false;
        } else if (!creationTime.equals(other.creationTime))
            return false;
        if (expires == null) {
            if (other.expires != null)
                return false;
        } else if (!expires.equals(other.expires))
            return false;
        if (expiryTime == null) {
            if (other.expiryTime != null)
                return false;
        } else if (!expiryTime.equals(other.expiryTime))
            return false;
        if (lastAccessTime == null) {
            if (other.lastAccessTime != null)
                return false;
        } else if (!lastAccessTime.equals(other.lastAccessTime))
            return false;
        if (lastModificationTime == null) {
            if (other.lastModificationTime != null)
                return false;
        } else if (!lastModificationTime.equals(other.lastModificationTime))
            return false;
        if (locationChanged == null) {
            if (other.locationChanged != null)
                return false;
        } else if (!locationChanged.equals(other.locationChanged))
            return false;
        if (usageCount != other.usageCount)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Times [lastModificationTime=" + lastModificationTime + ", creationTime=" + creationTime + "]";
    }

}
