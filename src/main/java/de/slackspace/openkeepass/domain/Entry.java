package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import de.slackspace.openkeepass.parser.TagParser;

/**
 * Represents an entry in the KeePass database. It typically consists of a title, username and a password.
 *
 */
@Root(strict = false, name = "Entry")
@Order(elements = {"UUID", "IconID", "CustomIconUUID", "ForegroundColor",
        "BackgroundColor", "Tags", "String", "Times", "History"})
public class Entry implements KeePassFileElement {

    private static final String USER_NAME = "UserName";
    private static final String NOTES = "Notes";
    private static final String URL = "URL";
    private static final String PASSWORD = "Password";
    private static final String TITLE = "Title";
    private static final List<String> PROPERTY_KEYS = new ArrayList<String>();

    public static Pattern REFERENCE_PATTERN = Pattern.compile("^\\{REF:(.)@I:([0-9a-zA-Z]*)\\}");

    static {
        PROPERTY_KEYS.add(USER_NAME);
        PROPERTY_KEYS.add(NOTES);
        PROPERTY_KEYS.add(URL);
        PROPERTY_KEYS.add(PASSWORD);
        PROPERTY_KEYS.add(TITLE);
    }

    @Element(name = "UUID")
    private UUID uuid;

    @Element(name = "IconID")
    private int iconId = 0;

    private transient byte[] iconData;

    @Element(name = "CustomIconUUID", required = false)
    private UUID customIconUUID;

    @ElementList(name = "String", inline = true, required = false)
    private List<Property> properties = new ArrayList<Property>();

    private List<Property> referencedProperties = new ArrayList<Property>();

    @Element(name = "History", required = false)
    private History history;

    @Element(name = "Times", required = false)
    private Times times;

    @Element(name = "Tags", required = false)
    private String tags;

    @Element(name = "ForegroundColor", required = false)
    private String foregroundColor;

    @Element(name = "BackgroundColor", required = false)
    private String backgroundColor;

    @ElementList(name = "Binary", inline = true, required = false)
    private List<Attachment> attachments = new ArrayList<Attachment>();

    private TagParser tagParser = new TagParser();

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
        this.tags = tagParser.toTagString(entryContract.getTags());
        this.foregroundColor = entryContract.getForegroundColor();
        this.backgroundColor = entryContract.getBackgroundColor();

        setValue(false, NOTES, entryContract.getNotes());
        setValue(true, PASSWORD, entryContract.getPassword());
        setValue(false, TITLE, entryContract.getTitle());
        setValue(false, USER_NAME, entryContract.getUsername());
        setValue(false, URL, entryContract.getUrl());

        this.properties.addAll(entryContract.getCustomPropertyList());
        this.attachments.addAll(entryContract.getAttachmentList());
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
     * Returns the raw data of either the custom icon (if specified) or the chosen stock icon.
     *
     * @return the raw icon data if available or null otherwise
     */
    public byte[] getIconData() {
        return iconData;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Property> getReferencedProperties() {
        return referencedProperties;
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    private void setValue(boolean isProtected, String propertyName, String propertyValue) {
        Property property = getPropertyByName(propertyName);
        if (property == null) {
            property = new Property(propertyName, propertyValue, isProtected);
            properties.add(property);
        }
        else {
            properties.remove(property);
            properties.add(new Property(propertyName, propertyValue, isProtected));
        }
    }

    private String getValueFromProperty(String name) {
        Property property = getPropertyByName(name);
        if (property == null) {
            return null;
        }

        String value = property.getValue();

        if (isUUIDReference(value)) {
            return getReferencedProperty(name, value);
        }

        return value;
    }

    private String getReferencedProperty(String name, String value) {
        Property referencedProperty = getPropertyByNameFromList(name, referencedProperties);
        if (referencedProperty == null) {
            return value;
        }

        return referencedProperty.getValue();
    }

    private boolean isUUIDReference(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        return Entry.REFERENCE_PATTERN.matcher(value).matches();
    }

    /**
     * Retrieves a property by it's name (ignores case)
     *
     * @param name the name of the property to find
     * @return the property if found, null otherwise
     */
    public Property getPropertyByName(String name) {
        return getPropertyByNameFromList(name, properties);
    }

    private Property getPropertyByNameFromList(String name, List<Property> list) {
        for (Property property : list) {
            if (property.getKey().equalsIgnoreCase(name)) {
                return property;
            }
        }

        return null;
    }

    public History getHistory() {
        return history;
    }

    public List<String> getTags() {
        if (tags != null) {
            return tagParser.fromTagString(tags);
        }

        return null;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customIconUUID == null) ? 0 : customIconUUID.hashCode());
        result = prime * result + ((history == null) ? 0 : history.hashCode());
        result = prime * result + iconId;
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((times == null) ? 0 : times.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Entry)) {
            return false;
        }
        Entry other = (Entry) obj;
        if (customIconUUID == null) {
            if (other.customIconUUID != null) {
                return false;
            }
        }
        else if (!customIconUUID.equals(other.customIconUUID)) {
            return false;
        }
        if (history == null) {
            if (other.history != null) {
                return false;
            }
        }
        else if (!history.equals(other.history)) {
            return false;
        }
        if (iconId != other.iconId) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        }
        else if (!properties.equals(other.properties)) {
            return false;
        }
        if (times == null) {
            if (other.times != null) {
                return false;
            }
        }
        else if (!times.equals(other.times)) {
            return false;
        }
        if (uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        }
        else if (!uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entry [uuid=" + uuid + ", getTitle()=" + getTitle() + ", getPassword()=" + getPassword()
                + ", getUsername()=" + getUsername() + "]";
    }

}
