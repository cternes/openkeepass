package de.slackspace.openkeepass.domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Entry {

	@XmlElement(name = "UUID")
	private String uuid;
	
	@XmlElement(name = "String")
	private Set<Property> properties;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Set<Property> getProperties() {
		return properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}
	
	public String getTitle() {
		return getValueFromProperty("Title");
	}
	
	public String getPassword() {
		return getValueFromProperty("Password");
	}
	
	public String getUrl() {
		return getValueFromProperty("URL");
	}
	
	public String getNotes() {
		return getValueFromProperty("Notes");
	}
	
	public String getUsername() {
		return getValueFromProperty("UserName");
	}
	
	private String getValueFromProperty(String name) {
		for (Property property : properties) {
			if(property.getKey().equalsIgnoreCase(name)) {
				return property.getValue();
			}
		}
		return null;
	}
	
}
