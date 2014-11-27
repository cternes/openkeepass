package de.slackspace.openkeepass.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.crypto.Salsa20;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeePassFile implements KeePassFileElement {

	@XmlElement(name = "Meta")
	private Meta meta;
	
	@XmlElement(name = "Root")
	private Group root;
	
	@XmlTransient
	private ProtectedStringCrypto protectedStringCrypto;

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	
	public Group getRoot() {
		return root;
	}
	
	public List<Group> getTopGroups() {
		if(root != null && root.getGroups() != null && root.getGroups().size() == 1) {
			return root.getGroups().get(0).getGroups();
		}
		return null;
	}
	
	public List<Entry> getTopEntries() {
		if(root != null && root.getGroups() != null && root.getGroups().size() == 1) {
			return root.getGroups().get(0).getEntries();
		}
		return null;
	}
	
	public void init(ProtectedStringCrypto protectedStringCrypto) {
		this.protectedStringCrypto = protectedStringCrypto;
		root.setParent(this);
	}
	
	public ProtectedStringCrypto getProtectedStringCrypto() {
		return protectedStringCrypto;
	}
}
