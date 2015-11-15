package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Entry implements KeePassFileElement {

	private static final String USER_NAME = "UserName";
	private static final String NOTES = "Notes";
	private static final String URL = "URL";
	private static final String PASSWORD = "Password";
	private static final String TITLE = "Title";

	@XmlTransient
	private KeePassFileElement parent;

	@XmlElement(name = "UUID")
	private String uuid;

	@XmlElement(name = "String")
	private List<Property> properties = new ArrayList<Property>();

	@XmlElement(name = "History")
	private History history;

	Entry() {
	}

	public Entry(String uuid) {
		setUuid(uuid);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getTitle() {
		return getValueFromProperty(TITLE);
	}

	public void setTitle(String title) {
		setValue(TITLE, title);
	}

	public String getPassword() {
		return getValueFromProperty(PASSWORD);
	}

	public void setPassword(String password) {
		setValue(PASSWORD, password);
	}

	public String getUrl() {
		return getValueFromProperty(URL);
	}

	public void setUrl(String url) {
		setValue(URL, url);
	}

	public String getNotes() {
		return getValueFromProperty(NOTES);
	}

	public void setNotes(String notes) {
		setValue(NOTES, notes);
	}

	public String getUsername() {
		return getValueFromProperty(USER_NAME);
	}

	public void setUsername(String username) {
		setValue(USER_NAME, username);
	}

	public boolean isTitleProtected() {
		return getPropertyByName(TITLE).isProtected();
	}

	public boolean isPasswordProtected() {
		return getPropertyByName(PASSWORD).isProtected();
	}

	public void setParent(KeePassFileElement element) {
		this.parent = element;

		for (Property property : properties) {
			property.setParent(this);
		}
	}

	private void setValue(String propertyName, String propertyValue) {
		Property property = getPropertyByName(propertyName);
		if (property == null) {
			property = new Property(propertyName, propertyValue, false);
			properties.add(property);
		} else {
			property.setValue(new PropertyValue(false, propertyValue));
		}
	}

	private String getValueFromProperty(String name) {
		Property property = getPropertyByName(name);
		if (property != null) {
			return property.getValue();
		}

		return null;
	}

	/**
	 * Retrieves a property by it's name (ignores case)
	 * 
	 * @param name the name of the property to find
	 * @return the property if found, null otherwise
	 */
	public Property getPropertyByName(String name) {
		for (Property property : properties) {
			if (property.getKey().equalsIgnoreCase(name)) {
				return property;
			}
		}

		return null;
	}

	public History getHistory() {
		return history;
	}

	public void setHistory(History history) {
		this.history = history;
	}

	@Override
	public String toString() {
		return "Entry [uuid=" + uuid + ", getTitle()=" + getTitle() + ", getPassword()=" + getPassword()
				+ ", getUsername()=" + getUsername() + "]";
	}

}
