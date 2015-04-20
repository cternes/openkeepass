package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class History {

	@XmlElement(name = "Entry")
	private List<Entry> entries = new ArrayList<Entry>();

	public List<Entry> getHistoricEntries() {
		return entries;
	}

	public void setHistoricEntries(List<Entry> historicEntries) {
		this.entries = historicEntries;
	}
}
