package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupBuilder implements GroupContract {

    private UUID uuid;

    private String name;

    private int iconId = 49;

    private Times times;

    private boolean isExpanded;

    private byte[] iconData;

    private UUID customIconUuid;

    private List<Entry> entries = new ArrayList<Entry>();

    private List<Group> groups = new ArrayList<Group>();

    public GroupBuilder() {
        this.uuid = UUID.randomUUID();
    }
    
    public GroupBuilder(UUID uuid) {
        this.uuid = uuid;
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

    public GroupBuilder addEntries(List<Entry> entryList) {
        entries.addAll(entryList);
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

    public GroupBuilder removeEntries(List<Entry> entryList) {
        entries.removeAll(entryList);
        return this;
    }

    public Group build() {
        return new Group(this);
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIconId() {
        return iconId;
    }

    @Override
    public Times getTimes() {
        return times;
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public byte[] getIconData() {
        return iconData;
    }

    @Override
    public UUID getCustomIconUuid() {
        return customIconUuid;
    }

    @Override
    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public List<Group> getGroups() {
        return groups;
    }

}
