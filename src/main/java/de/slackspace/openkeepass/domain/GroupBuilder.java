package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupBuilder {

	KeePassFileElement parent;
	
	String uuid;
	
	String name;
	
	int iconId = 49; 
	
	Times times;
	
	boolean isExpanded;
	
	List<Entry> entries = new ArrayList<Entry>();

	List<Group> groups = new ArrayList<Group>();
	
	public GroupBuilder() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	public GroupBuilder(String name) {
		this();
		this.name = name;
	}
	
	public GroupBuilder name(String name) {
		this.name = name;
		return this;
	}

	public GroupBuilder iconId(int iconId) {
		this.iconId = iconId;
		return this;
	}
	
	public GroupBuilder times(Times times) {
		this.times = times;
		return this;
	}
	
	public GroupBuilder isExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
		return this;
	}
	
	public GroupBuilder addEntry(Entry entry) {
		entries.add(entry);
		return this;
	}
	
	public GroupBuilder addGroup(Group group) {
		groups.add(group);
		return this;
	}
	
	public GroupBuilder parent(KeePassFileElement parent) {
		this.parent = parent;
		return this;
	}
	
	public Group build() {
		return new Group(this);
	}
	
}
