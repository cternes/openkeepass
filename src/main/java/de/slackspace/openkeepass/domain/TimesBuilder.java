package de.slackspace.openkeepass.domain;

import java.util.Calendar;

public class TimesBuilder {

	Calendar lastModificationTime;

	Calendar creationTime;
	
	Calendar lastAccessTime;

	Calendar expiryTime;
	
	Boolean expires;
	
	int usageCount;
	
	Calendar locationChanged;
	
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

	public TimesBuilder expires(Boolean expires) {
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

	public Times build() {
		return new Times(this);
	}
}
