package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.xml.BooleanXmlAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyValue {

	@XmlAttribute(name = "Protected")
	@XmlJavaTypeAdapter(BooleanXmlAdapter.class)
	private Boolean isProtected;

	@XmlValue
	private String value;
	
	PropertyValue() { }
	
	public PropertyValue(boolean isProtected, String value) {
		setProtected(isProtected);
		setValue(value);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isProtected() {
		if(isProtected == null) {
			return false;
		}
		return isProtected.booleanValue();
	}
	
	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	@Override
	public String toString() {
		return "PropertyValue [value=" + value + "]";
	}
	
}
