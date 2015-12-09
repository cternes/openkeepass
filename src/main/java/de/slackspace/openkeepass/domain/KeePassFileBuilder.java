package de.slackspace.openkeepass.domain;

public class KeePassFileBuilder {

	private Meta meta;
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

	public KeePassFileBuilder withTopGroup(Group group) {
		rootBuilder = rootBuilder.addGroup(group);
		
		return this;
	}
	
	public KeePassFileBuilder withTopEntries(Entry... entries) {
		for (Entry entry : entries) {
			topGroupBuilder.addEntry(entry);
		}
		
		return this;
	}
	
	public KeePassFile build() {
		KeePassFile keePassFile = new KeePassFile();
		keePassFile.setMeta(meta);
		
		setTopGroupNameIfNotExisting();
		keePassFile.setRoot(rootBuilder.build());
		
		return keePassFile;
	}
	
	private void setTopGroupNameIfNotExisting() {
		if(rootBuilder.groups.isEmpty()) {
			rootBuilder.addGroup(topGroupBuilder.name(meta.getDatabaseName())
					.build());
		}
	}
}
