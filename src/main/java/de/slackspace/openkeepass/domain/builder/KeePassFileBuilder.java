package de.slackspace.openkeepass.domain.builder;

import java.util.Arrays;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.Meta;

public class KeePassFileBuilder {

	private Meta meta;
	private Group root = new Group();
	
	public KeePassFileBuilder(String databaseName) {
		meta = new Meta();
		meta.setDatabaseName(databaseName);
		meta.setGenerator("KeePass");
		meta.setHistoryMaxItems(10);
	}
	
	public KeePassFileBuilder(Meta meta) {
		this.meta = meta;
	}

	public KeePassFileBuilder withTopGroup(Group group) {
		root.getGroups().add(group);
		
		return this;
	}
	
	public KeePassFileBuilder withTopEntries(Entry... entries) {
		addTopGroupIfNotExisting();
		
		Group topGroup = root.getGroups().get(0);
		topGroup.setEntries(Arrays.asList(entries));
		
		return this;
	}
	
	public KeePassFile build() {
		KeePassFile keePassFile = new KeePassFile();
		keePassFile.setMeta(meta);
		
		addTopGroupIfNotExisting();
		setTopGroupNameIfNotExisting();
		keePassFile.setRoot(root);
		
		return keePassFile;
	}
	
	private void setTopGroupNameIfNotExisting() {
		Group topGroup = root.getGroups().get(0);
		String name = topGroup.getName();
		if(name == null || name.isEmpty()) {
			topGroup.setName(meta.getDatabaseName());
		}
	}

	private void addTopGroupIfNotExisting() {
		if(root.getGroups().isEmpty()) {
			Group topGroup = new Group();
			topGroup.setName(meta.getDatabaseName());
			root.getGroups().add(topGroup);
		}
	}
}
