package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Meta {
	
	@XmlElement(name = "DatabaseName")
	private String databaseName;
	
	@XmlElement(name = "DatabaseDescription")
	private String databaseDescription;

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseDescription() {
		return databaseDescription;
	}

	public void setDatabaseDescription(String databaseDescription) {
		this.databaseDescription = databaseDescription;
	}
}
