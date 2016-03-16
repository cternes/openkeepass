package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupBuilder {

    UUID uuid;

    String name;

    int iconId = 49;

    Times times;

    boolean isExpanded;

    byte[] iconData;

    UUID customIconUuid;

    List<Entry> entries = new ArrayList<Entry>();

    List<Group> groups = new ArrayList<Group>();

    public GroupBuilder() {
        this.uuid = UUID.randomUUID();
    }

    public GroupBuilder(String name) {
        this();
        this.name = name;
    }

    public GroupBuilder(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Parameter group must not be null");
        }

        this.uuid = group.getUuid();
        this.name = group.getName();
        this.iconId = group.getIconId();
        this.iconData = group.getIconData();
        this.customIconUuid = group.getCustomIconUuid();
        this.times = group.getTimes();
        this.isExpanded = group.isExpanded();
        this.groups = group.getGroups();
        this.entries = group.getEntries();
    }

    public GroupBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder iconId(int iconId) {
        this.iconId = iconId;
        return this;
    }

    public GroupBuilder iconData(byte[] iconData) {
        this.iconData = iconData;
        return this;
    }

    public GroupBuilder customIconUuid(UUID uuid) {
        this.customIconUuid = uuid;
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

    public GroupBuilder removeGroup(Group group) {
        groups.remove(group);
        return this;
    }

    public GroupBuilder removeEntry(Entry entry) {
        entries.remove(entry);
        return this;
    }

    public Group build() {
        return new Group(this);
    }

}
