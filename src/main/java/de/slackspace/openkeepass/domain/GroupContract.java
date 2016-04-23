package de.slackspace.openkeepass.domain;

import java.util.List;
import java.util.UUID;

public interface GroupContract {

    UUID getUuid();

    String getName();

    int getIconId();

    Times getTimes();

    boolean isExpanded();

    byte[] getIconData();

    UUID getCustomIconUuid();

    List<Entry> getEntries();

    List<Group> getGroups();
}
