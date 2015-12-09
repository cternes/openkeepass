package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Property implements KeePassFileElement {

	@XmlTransient
	private KeePassFileElement parent;
	
	@XmlElement(name = "Key")
	private String key;
	
	@XmlElement(name = "Value")
	private PropertyValue propertyValue;
	
	Property() { }
	
	public Property(String key, String value, boolean isProtected) {
		this.key = key;
		this.propertyValue = new PropertyValue(isProtected, value);
	}
	
	public String getKey() {
		return key;
	}

	public String getValue() {
		return propertyValue.getValue();
	}
	
	public boolean isProtected() {
		return propertyValue.isProtected();
	}

	public PropertyValue getPropertyValue() {
		return propertyValue;
	}

	@Override
	public String toString() {
		return "Property [key=" + key + ", propertyValue=" + propertyValue + "]";
	}

}
