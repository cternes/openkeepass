package de.slackspace.openkeepass.domain;

import java.util.List;
import java.util.UUID;

public interface EntryContract {

    UUID getUuid();

    byte[] getIconData();

    int getIconId();

    UUID getCustomIconUUID();

    String getTitle();

    String getUsername();

    String getPassword();

    String getNotes();

    String getUrl();

    List<Property> getCustomPropertyList();

    History getHistory();

    Times getTimes();
}
