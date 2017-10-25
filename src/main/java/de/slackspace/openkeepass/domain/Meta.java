package de.slackspace.openkeepass.domain;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents the metadata of the KeePass database like database name, custom
 * icons or how much history entries will be preserved.
 *
 */
@Root(strict = false)
public class Meta {

    @Element(name = "Generator", required = false)
    private String generator;

    @Element(name = "DatabaseName", required = false)
    private String databaseName;

    @Element(name = "DatabaseDescription", required = false)
    private String databaseDescription;

    @Element(name = "DatabaseNameChanged", type = GregorianCalendar.class, required = false)
    private Calendar databaseNameChanged;

    @Element(name = "DatabaseDescriptionChanged", type = GregorianCalendar.class, required = false)
    private Calendar databaseDescriptionChanged;

    @Element(name = "MaintenanceHistoryDays", required = false)
    private int maintenanceHistoryDays;

    @Element(name = "RecycleBinUUID", required = false)
    private UUID recycleBinUuid;

    @Element(name = "RecycleBinChanged", type = GregorianCalendar.class, required = false)
    private Calendar recycleBinChanged;

    @Element(name = "RecycleBinEnabled", required = false)
    private Boolean recycleBinEnabled;

    @Element(name = "HistoryMaxItems", required = false)
    private long historyMaxItems;

    @Element(name = "HistoryMaxSize", required = false)
    private long historyMaxSize;

    @Element(name = "CustomIcons", required = false)
    private CustomIcons customIcons;

    @Element(name = "Binaries", required = false)
    private Binaries binaries;

    Meta() {
    }

    public Meta(MetaContract metaContract) {
        this.databaseDescription = metaContract.getDatabaseDescription();
        this.databaseDescriptionChanged = metaContract.getDatabaseDescriptionChanged();
        this.databaseName = metaContract.getDatabaseName();
        this.databaseNameChanged = metaContract.getDatabaseNameChanged();
        this.generator = metaContract.getGenerator();
        this.historyMaxItems = metaContract.getHistoryMaxItems();
        this.historyMaxSize = metaContract.getHistoryMaxSize();
        this.maintenanceHistoryDays = metaContract.getMaintenanceHistoryDays();
        this.recycleBinChanged = metaContract.getRecycleBinChanged();
        this.recycleBinEnabled = metaContract.getRecycleBinEnabled();
        this.recycleBinUuid = metaContract.getRecycleBinUuid();
        this.customIcons = metaContract.getCustomIcons();
        this.binaries = metaContract.getBinaries();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseDescription() {
        return databaseDescription;
    }

    public String getGenerator() {
        return generator;
    }

    public Calendar getDatabaseNameChanged() {
        return databaseNameChanged;
    }

    public Calendar getDatabaseDescriptionChanged() {
        return databaseDescriptionChanged;
    }

    public int getMaintenanceHistoryDays() {
        return maintenanceHistoryDays;
    }

    public UUID getRecycleBinUuid() {
        return recycleBinUuid;
    }

    public Calendar getRecycleBinChanged() {
        return recycleBinChanged;
    }

    public long getHistoryMaxItems() {
        return historyMaxItems;
    }

    public long getHistoryMaxSize() {
        return historyMaxSize;
    }

    public boolean getRecycleBinEnabled() {
        return recycleBinEnabled;
    }

    public CustomIcons getCustomIcons() {
        return customIcons;
    }

    public Binaries getBinaries() {
        return binaries;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customIcons == null) ? 0 : customIcons.hashCode());
        result = prime * result + ((databaseDescription == null) ? 0 : databaseDescription.hashCode());
        result = prime * result + ((databaseDescriptionChanged == null) ? 0 : databaseDescriptionChanged.hashCode());
        result = prime * result + ((databaseName == null) ? 0 : databaseName.hashCode());
        result = prime * result + ((databaseNameChanged == null) ? 0 : databaseNameChanged.hashCode());
        result = prime * result + ((generator == null) ? 0 : generator.hashCode());
        result = prime * result + (int) (historyMaxItems ^ (historyMaxItems >>> 32));
        result = prime * result + (int) (historyMaxSize ^ (historyMaxSize >>> 32));
        result = prime * result + maintenanceHistoryDays;
        result = prime * result + ((recycleBinChanged == null) ? 0 : recycleBinChanged.hashCode());
        result = prime * result + ((recycleBinEnabled == null) ? 0 : recycleBinEnabled.hashCode());
        result = prime * result + ((recycleBinUuid == null) ? 0 : recycleBinUuid.hashCode());
        result = prime * result + ((binaries == null) ? 0 : binaries.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Meta))
            return false;
        Meta other = (Meta) obj;
        if (customIcons == null) {
            if (other.customIcons != null)
                return false;
        } else if (!customIcons.equals(other.customIcons))
            return false;
        if (databaseDescription == null) {
            if (other.databaseDescription != null)
                return false;
        } else if (!databaseDescription.equals(other.databaseDescription))
            return false;
        if (databaseDescriptionChanged == null) {
            if (other.databaseDescriptionChanged != null)
                return false;
        } else if (!databaseDescriptionChanged.equals(other.databaseDescriptionChanged))
            return false;
        if (databaseName == null) {
            if (other.databaseName != null)
                return false;
        } else if (!databaseName.equals(other.databaseName))
            return false;
        if (databaseNameChanged == null) {
            if (other.databaseNameChanged != null)
                return false;
        } else if (!databaseNameChanged.equals(other.databaseNameChanged))
            return false;
        if (generator == null) {
            if (other.generator != null)
                return false;
        } else if (!generator.equals(other.generator))
            return false;
        if (historyMaxItems != other.historyMaxItems)
            return false;
        if (historyMaxSize != other.historyMaxSize)
            return false;
        if (maintenanceHistoryDays != other.maintenanceHistoryDays)
            return false;
        if (recycleBinChanged == null) {
            if (other.recycleBinChanged != null)
                return false;
        } else if (!recycleBinChanged.equals(other.recycleBinChanged))
            return false;
        if (recycleBinEnabled == null) {
            if (other.recycleBinEnabled != null)
                return false;
        } else if (!recycleBinEnabled.equals(other.recycleBinEnabled))
            return false;
        if (recycleBinUuid == null) {
            if (other.recycleBinUuid != null)
                return false;
        } else if (!recycleBinUuid.equals(other.recycleBinUuid))
            return false;
        if (binaries == null) {
            if (other.binaries != null)
                return false;
        } else if (!binaries.equals(other.binaries))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Meta [generator=" + generator + ", databaseName=" + databaseName + ", databaseDescription=" + databaseDescription + "]";
    }
}
