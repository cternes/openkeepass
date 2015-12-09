package de.slackspace.openkeepass.domain;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.xml.BooleanXmlAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Times {

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

	public Calendar getLastModificationTime() {
		return lastModificationTime;
	}

	public void setLastModificationTime(Calendar lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}

	public Calendar getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}

	public Calendar getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Calendar lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public Calendar getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Calendar expiryTime) {
		this.expiryTime = expiryTime;
	}

	public boolean isExpires() {
		return expires;
	}

	public void setExpires(boolean expires) {
		this.expires = expires;
	}

	public int getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}

	public Calendar getLocationChanged() {
		return locationChanged;
	}

	public void setLocationChanged(Calendar locationChanged) {
		this.locationChanged = locationChanged;
	}
	
}
