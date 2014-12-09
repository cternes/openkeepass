package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.filter.Filter;
import de.slackspace.openkeepass.filter.ListFilter;

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
		return new ArrayList<Group>();
	}
	
	public List<Entry> getTopEntries() {
		if(root != null && root.getGroups() != null && root.getGroups().size() == 1) {
			return root.getGroups().get(0).getEntries();
		}
		return new ArrayList<Entry>();
	}
	
	public Entry getEntryByTitle(String title) {
		List<Entry> entries = getEntriesByTitle(title, true);
		
		if(entries != null) {
			return entries.get(0);
		}
		
		return null;
	}
	
	public List<Entry> getEntriesByTitle(final String title, final boolean matchExactly) {
		List<Entry> allEntries = new ArrayList<Entry>();
		
		if(root != null) {
			getEntries(root, allEntries);
		}
		
		return ListFilter.filter(allEntries, new Filter<Entry>() {

			@Override
			public boolean matches(Entry item) {
				if(matchExactly) {
					if(item.getTitle() != null && item.getTitle().equalsIgnoreCase(title)) {
						return true;
					}	
				}
				else {
					if(item.getTitle() != null && item.getTitle().contains(title)) {
						return true;
					}
				}

				return false;
			}
			
		});
	}
	
	public void init(ProtectedStringCrypto protectedStringCrypto) {
		this.protectedStringCrypto = protectedStringCrypto;
		root.setParent(this);
	}
	
	public ProtectedStringCrypto getProtectedStringCrypto() {
		return protectedStringCrypto;
	}
	
	public List<Entry> getEntries() {
		List<Entry> allEntries = new ArrayList<Entry>();
		
		if(root != null) {
			getEntries(root, allEntries);
		}
		
		return allEntries;
	}
	
	private void getEntries(Group parentGroup, List<Entry> entries) {
		List<Group> groups = parentGroup.getGroups();
		entries.addAll(parentGroup.getEntries());
		
		if(groups.size() != 0) {
			for (Group group : groups) {
				getEntries(group, entries);
			}
		}
		
		return;
	}
	
}
