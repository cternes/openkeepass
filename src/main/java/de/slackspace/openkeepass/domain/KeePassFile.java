package de.slackspace.openkeepass.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeePassFile {

	@XmlElement(name = "Meta")
	private Meta meta;
	
	@XmlElement(name = "Root")
	private Group root;

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	
	public Group getRoot() {
		return root;
	}
	
	public List<Group> getGroups() {
		if(root != null && root.getGroups() != null && root.getGroups().size() == 1) {
			return root.getGroups().get(0).getGroups();
		}
		return null;
	}
	
	public List<Entry> getEntries() {
		if(root != null && root.getGroups() != null && root.getGroups().size() == 1) {
			return root.getGroups().get(0).getEntries();
		}
		return null;
	}
}
