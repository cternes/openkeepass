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

/**
 * A KeePassFile represents the structure of a KeePass database. This is the central entry point to read data from the KeePass database.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeePassFile implements KeePassFileElement {

	@XmlElement(name = "Meta")
	private Meta meta;
	
	@XmlElement(name = "Root")
	private Group root;
	
	@XmlTransient
	private ProtectedStringCrypto protectedStringCrypto;

	/**
	 * Retrieves the meta section of a KeePass database.
	 * 
	 * @return the meta section of the database
	 * @see Meta
	 */
	public Meta getMeta() {
		return meta;
	}

	/**
	 * Sets the meta section of a KeePass database.
	 * 
	 * @param meta the meta section of the database
	 */
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	
	/**
	 * Retrieves the root group of a KeePass database.
	 * 
	 * @return the root group
	 * @see Group
	 */
	public Group getRoot() {
		return root;
	}
	
	/**
	 * Sets the root group of a KeePass database.
	 * 
	 * @param root the root group
	 */
	public void setRoot(Group root) {
		this.root = root;
	}
	
	/**
	 * Retrieves all groups at the root level of a KeePass database. 
	 * 
	 * @return a list of root level groups
	 * @see Group
	 */
	public List<Group> getTopGroups() {
		if(root != null && root.getGroups() != null && root.getGroups().size() == 1) {
			return root.getGroups().get(0).getGroups();
		}
		return new ArrayList<Group>();
	}
	
	/**
	 * Retrieves all entries at the root level of a KeePass database.
	 * 
	 * @return a list of root level entries
	 * @see Entry
	 */
	public List<Entry> getTopEntries() {
		if(root != null && root.getGroups() != null && root.getGroups().size() == 1) {
			return root.getGroups().get(0).getEntries();
		}
		return new ArrayList<Entry>();
	}
	
	/**
	 * Retrieves a single entry with an exactly matching title.
	 * <p>
	 * If there are multiple entries with the same title, the first one found will be returned.
	 * 
	 * @param title the title which should be searched
	 * @return an entry with a matching title 
	 * @see Entry
	 */
	public Entry getEntryByTitle(String title) {
		List<Entry> entries = getEntriesByTitle(title, true);
		
		if(entries != null) {
			return entries.get(0);
		}
		
		return null;
	}
	
	/**
	 * Retrieves a list of entries with matching titles. 
	 * <p>
	 * If the <tt>matchExactly</tt> flag is true, only entries which have an exactly matching title will be returned,
	 * otherwise all entries which contain the given title will be returned.
	 * 
	 * @param title the title which should be searched
	 * @param matchExactly if true only entries which have an exactly matching title will be returned
	 * @return a list of entries with matching titles
	 * @see Entry
	 */
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
	
	public void init() {
		root.setParent(this);
	}
	
	/**
	 * Retrieves a list of all entries in the KeePass database.
	 * 
	 * @return a list of all entries
	 * @see Entry
	 */
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
