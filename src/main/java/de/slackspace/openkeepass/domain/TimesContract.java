package de.slackspace.openkeepass.domain;

import java.util.Calendar;

public interface TimesContract {

    Calendar getLastModificationTime();

    Calendar getCreationTime();

    Calendar getLastAccessTime();

    Calendar getExpiryTime();

    boolean getExpires();

    int getUsageCount();

    Calendar getLocationChanged();
}
