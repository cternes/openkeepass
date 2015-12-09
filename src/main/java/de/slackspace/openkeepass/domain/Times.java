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

	Times() {}
	
	public Times(TimesBuilder timesBuilder) {
		this.creationTime = timesBuilder.creationTime;
		this.expires = timesBuilder.expires;
		this.expiryTime = timesBuilder.expiryTime;
		this.lastAccessTime = timesBuilder.lastAccessTime;
		this.lastModificationTime = timesBuilder.lastModificationTime;
		this.locationChanged = timesBuilder.locationChanged;
		this.usageCount = timesBuilder.usageCount;
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
}
