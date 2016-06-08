package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.domain.xml.adapter.UUIDXmlAdapter;

/**
 * Represents an entry in the KeePass database. It typically consists of a
 * title, username and a password.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Entry implements KeePassFileElement, Cloneable {

    private static final String USER_NAME = "UserName";
    private static final String NOTES = "Notes";
    private static final String URL = "URL";
    private static final String PASSWORD = "Password";
    private static final String TITLE = "Title";
    private static final List<String> PROPERTY_KEYS = new ArrayList<String>();

    static {
        PROPERTY_KEYS.add(USER_NAME);
        PROPERTY_KEYS.add(NOTES);
        PROPERTY_KEYS.add(URL);
        PROPERTY_KEYS.add(PASSWORD);
        PROPERTY_KEYS.add(TITLE);
    }

    @XmlElement(name = "UUID")
    @XmlJavaTypeAdapter(UUIDXmlAdapter.class)
    private UUID uuid;

    @XmlElement(name = "IconID")
    private int iconId = 0;

    private transient byte[] iconData;

    @XmlElement(name = "CustomIconUUID")
    @XmlJavaTypeAdapter(UUIDXmlAdapter.class)
    private UUID customIconUUID;

    @XmlElement(name = "String")
    private List<Property> properties = new ArrayList<Property>();

    @XmlElement(name = "History")
    private History history;

    @XmlElement(name = "Times")
    private Times times;

    Entry() {
        this.uuid = UUID.randomUUID();
    }

    public Entry(EntryContract entryContract) {
        this.history = entryContract.getHistory();
        this.uuid = entryContract.getUuid();
        this.iconData = entryContract.getIconData();
        this.iconId = entryContract.getIconId();
        this.customIconUUID = entryContract.getCustomIconUUID();
        this.times = entryContract.getTimes();

        setValue(false, NOTES, entryContract.getNotes());
        setValue(true, PASSWORD, entryContract.getPassword());
        setValue(false, TITLE, entryContract.getTitle());
        setValue(false, USER_NAME, entryContract.getUsername());
        setValue(false, URL, entryContract.getUrl());

        this.properties.addAll(entryContract.getCustomPropertyList());
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the icon id of this group.
     *
     * @return the icon id of this group
     */
    public int getIconId() {
        return iconId;
    }

    /**
     * Retrieves the custom icon of this group.
     *
     * @return the uuid of the custom icon or null
     */
    public UUID getCustomIconUuid() {
        return customIconUUID;
    }

    /**
     * Returns the raw data of either the custom icon (if specified) or the
     * chosen stock icon.
     *
     * @return the raw icon data if available or null otherwise
     */
    public byte[] getIconData() {
        return iconData;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Property> getCustomProperties() {
        List<Property> customProperties = new ArrayList<Property>();

        for (Property property : properties) {
            if (!PROPERTY_KEYS.contains(property.getKey())) {
                customProperties.add(property);
            }
        }

        return customProperties;
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

    public Times getTimes() {
        return times;
    }

    private void setValue(boolean isProtected, String propertyName, String propertyValue) {
        Property property = getPropertyByName(propertyName);
        if (property == null) {
            property = new Property(propertyName, propertyValue, isProtected);
            properties.add(property);
        } else {
            properties.remove(property);
            properties.add(new Property(propertyName, propertyValue, isProtected));
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
     * @param name
     *            the name of the property to find
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
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((history == null) ? 0 : history.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((times == null) ? 0 : times.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Entry))
            return false;
        Entry other = (Entry) obj;
        if (history == null) {
            if (other.history != null)
                return false;
        } else if (!history.equals(other.history))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (times == null) {
            if (other.times != null)
                return false;
        } else if (!times.equals(other.times))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Entry [uuid=" + uuid + ", getTitle()=" + getTitle() + ", getPassword()=" + getPassword() + ", getUsername()=" + getUsername() + "]";
    }

    
    @Override
    protected Object clone() throws CloneNotSupportedException {
    	Entry ret = new Entry();
    	ret.uuid = this.uuid;
    	ret.iconId = this.iconId;
    	if(this.iconData!=null){
        	ret.iconData = Arrays.copyOf(this.iconData, this.iconData.length);
    	}
    	ret.customIconUUID = this.customIconUUID;
    	
    	for(Property property:properties){
    		ret.properties.add((Property) property.clone());
    	}
    	if(this.history!=null){
        	ret.history = (History) this.history.clone();
    	}
    	if(this.times!=null){
        	ret.times = (Times) this.times.clone();
    	}
    	return ret;
    }
}
