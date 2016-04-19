package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyFile {

	@XmlElement(name = "Key")
	private Key key;

	private boolean isXmlFile = true;

	public KeyFile() {
	}

	public KeyFile(boolean isXmlFile) {
		this.isXmlFile = isXmlFile;
	}

	public Key getKey() {
		return key;
	}

	public boolean isXmlFile() {
		return isXmlFile;
	}

}
