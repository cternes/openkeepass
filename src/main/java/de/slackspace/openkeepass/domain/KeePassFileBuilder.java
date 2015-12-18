package de.slackspace.openkeepass.domain;

import de.slackspace.openkeepass.domain.zipper.GroupZipper;

public class KeePassFileBuilder {

	Meta meta;
	Group root;
	private GroupBuilder rootBuilder = new GroupBuilder();
	private GroupBuilder topGroupBuilder = new GroupBuilder();
	private KeePassFile keePassFile;
	
	public KeePassFileBuilder(KeePassFile keePassFile) {
		this.keePassFile = keePassFile;
		this.meta = keePassFile.getMeta();
		
		rootBuilder = new GroupBuilder(keePassFile.getRoot());
	}
	
	public KeePassFileBuilder(String databaseName) {
		meta = new MetaBuilder(databaseName)
				.historyMaxItems(10)
				.build();
	}
	
	public KeePassFileBuilder(Meta meta) {
		this.meta = meta;
	}

	public KeePassFileBuilder addTopGroups(Group... groups) {
		for (Group group : groups) {
			rootBuilder.addGroup(group);
		}
		
		return this;
	}
	
	public KeePassFileBuilder addTopEntries(Entry... entries) {
		for (Entry entry : entries) {
			topGroupBuilder.addEntry(entry);
		}
		
		return this;
	}
	
	public KeePassFile build() {
		setTopGroupNameIfNotExisting();
		
		root = rootBuilder.build();
		
		return new KeePassFile(this);
	}
	
	public GroupZipper getZipper() {
		return new GroupZipper(keePassFile);
	}
	
	private void setTopGroupNameIfNotExisting() {
		if(rootBuilder.groups.isEmpty()) {
			rootBuilder.addGroup(topGroupBuilder.name(meta.getDatabaseName())
					.build());
		}
	}
}
