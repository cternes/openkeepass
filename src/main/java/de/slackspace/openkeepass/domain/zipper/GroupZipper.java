package de.slackspace.openkeepass.domain.zipper;

import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;
import de.slackspace.openkeepass.domain.Meta;

public class GroupZipper {

	private Meta meta;
	private int index = 0;
	private Group node;
	private GroupZipper parent;
	
	public GroupZipper(KeePassFile keePassFile) {
		this.meta = keePassFile.getMeta();
		this.node = keePassFile.getRoot().getGroups().get(0);
	}
	
	private GroupZipper(GroupZipper parent, Group group) {
		this.parent = parent;
		this.node = group;
	}
	
	public GroupZipper down() {
		if(node.getGroups() == null || node.getGroups().isEmpty()) {
			throw new RuntimeException("Could not move down because this group does not have any children");
		}
		
		index = 0;
		parent = new GroupZipper(parent, node);
		node = node.getGroups().get(0);
		
		return this;
	}
	
	public GroupZipper up() {
		if(parent == null) {
			throw new RuntimeException("Could not move up because this group does not have a parent");
		}
		
		return parent;
	}
	
	public GroupZipper right() {
		index++;
		node = parent.getNode().getGroups().get(index);
		
		return this;
	}
	
	public GroupZipper left() {
		index--;
		node = parent.getNode().getGroups().get(index);
		
		return this;
	}
	
	public Group getNode() {
		return node;
	}

	public GroupZipper replace(Group group) {
		parent.getNode().getGroups().set(index, group);
		
		return this;
	}
	
	public KeePassFile close() {
		Group rootNode = getRoot();
		
		return new KeePassFileBuilder(meta).addTopGroups(rootNode).build();
	}
	
	public Group getRoot() {
		if(parent == null) {
			return node;
		}
		
		return parent.getRoot();
	}
}
