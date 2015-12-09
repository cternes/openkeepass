package de.slackspace.openkeepass.domain;

public class KeePassFileBuilder {

	Meta meta;
	Group root;
	private GroupBuilder rootBuilder = new GroupBuilder();
	private GroupBuilder topGroupBuilder = new GroupBuilder();
	
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
	
	private void setTopGroupNameIfNotExisting() {
		if(rootBuilder.groups.isEmpty()) {
			rootBuilder.addGroup(topGroupBuilder.name(meta.getDatabaseName())
					.build());
		}
	}
}
