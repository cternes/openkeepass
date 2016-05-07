package de.slackspace.openkeepass.domain;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.domain.xml.adapter.BooleanXmlAdapter;

/**
 * Represents statistical information of an {@link Entry}.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Times implements Cloneable{

    @XmlElement(name = "LastModificationTime")
    private Calendar lastModificationTime;

    @XmlElement(name = "CreationTime")
    private Calendar creationTime;

    @XmlElement(name = "LastAccessTime")
    private Calendar lastAccessTime;

    @XmlElement(name = "ExpiryTime")
    private Calendar expiryTime;

    @XmlElement(name = "Expires")
    @XmlJavaTypeAdapter(BooleanXmlAdapter.class)
    private Boolean expires;

    @XmlElement(name = "UsageCount")
    private int usageCount;

    @XmlElement(name = "LocationChanged")
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

    
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Times ret = new Times();
		if(this.lastModificationTime!=null){
			ret.lastModificationTime = (Calendar)this.lastModificationTime.clone();
		}
		if(this.creationTime!=null){
			ret.creationTime = (Calendar)this.creationTime.clone();
		}
		if(this.lastAccessTime!=null){
			ret.lastAccessTime = (Calendar)this.lastAccessTime.clone();
		}
		if(this.expiryTime!=null){
			ret.expiryTime = (Calendar)this.expiryTime.clone();
		}
		ret.expires = this.expires;
		ret.usageCount = this.usageCount;
		
		if(this.locationChanged!=null){
			ret.locationChanged = (Calendar)this.locationChanged.clone();
		}
		return ret;
	}    
}
