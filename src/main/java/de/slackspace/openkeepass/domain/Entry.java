package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.xml.UUIDXmlAdapter;

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
	@XmlJavaTypeAdapter(UUIDXmlAdapter.class)
	private String uuid;

	@XmlElement(name = "String")
	private List<Property> properties = new ArrayList<Property>();

	@XmlElement(name = "History")
	private History history;

	Entry() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	public Entry(EntryBuilder builder) {
		this.parent = builder.parent;
		this.history = builder.history; 
		this.uuid = builder.uuid;
		
		setValue(false, NOTES, builder.notes);
		setValue(true, PASSWORD, builder.password);
		setValue(false, TITLE, builder.title);
		setValue(false, USER_NAME, builder.username);
		setValue(false, URL, builder.url);
	}

	public String getUuid() {
		return uuid;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public String getTitle() {
		return getValueFromProperty(TITLE);
	}

	public String getPassword() {
		return getValueFromProperty(PASSWORD);
	}

	public String getUrl() {
		return getValueFromProperty(URL);
	}

	public String getNotes() {
		return getValueFromProperty(NOTES);
	}

	public String getUsername() {
		return getValueFromProperty(USER_NAME);
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

	private void setValue(boolean isProtected, String propertyName, String propertyValue) {
		Property property = getPropertyByName(propertyName);
		if (property == null) {
			property = new Property(propertyName, propertyValue, isProtected);
			properties.add(property);
		} else {
			property.setValue(new PropertyValue(isProtected, propertyValue));
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

	@Override
	public String toString() {
		return "Entry [uuid=" + uuid + ", getTitle()=" + getTitle() + ", getPassword()=" + getPassword()
				+ ", getUsername()=" + getUsername() + "]";
	}

}
