package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

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
		setKey(key);
		setValue(new PropertyValue(isProtected, value));
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return propertyValue.getValue();
	}
	
	public void setValue(PropertyValue value) {
		this.propertyValue = value;
	}

	public boolean isProtected() {
		return propertyValue.isProtected();
	}

	public void setParent(KeePassFileElement element) {
		this.parent = element;
	}

}
