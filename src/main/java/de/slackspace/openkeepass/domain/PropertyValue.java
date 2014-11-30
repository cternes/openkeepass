package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyValue {

	@XmlAttribute(name = "Protected")
	private String isProtected;

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
		
		return isProtected.equalsIgnoreCase("true");
	}
	
	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected == true ? "True" : "False";
	}
	
}
