package de.slackspace.openkeepass.domain;

import java.util.Calendar;
import java.util.UUID;

public interface MetaContract {

    String getGenerator();

    String getDatabaseName();

    String getDatabaseDescription();

    Calendar getDatabaseNameChanged();

    Calendar getDatabaseDescriptionChanged();

    int getMaintenanceHistoryDays();

    UUID getRecycleBinUuid();

    Calendar getRecycleBinChanged();

    boolean getRecycleBinEnabled();

    long getHistoryMaxItems();

    long getHistoryMaxSize();

    CustomIcons getCustomIcons();

    Binaries getBinaries();
}
