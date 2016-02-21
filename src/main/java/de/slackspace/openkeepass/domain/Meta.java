package de.slackspace.openkeepass.domain;

import java.util.Calendar;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.domain.xml.adapter.BooleanXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.UUIDXmlAdapter;

/**
 * Represents the metadata of the KeePass database like database name, custom
 * icons or how much history entries will be preserved.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Meta {

	@XmlElement(name = "Generator")
	private String generator;

	@XmlElement(name = "DatabaseName")
	private String databaseName;

	@XmlElement(name = "DatabaseDescription")
	private String databaseDescription;

	@XmlElement(name = "DatabaseNameChanged")
	private Calendar databaseNameChanged;

	@XmlElement(name = "DatabaseDescriptionChanged")
	private Calendar databaseDescriptionChanged;

	@XmlElement(name = "MaintenanceHistoryDays")
	private int maintenanceHistoryDays;

	@XmlElement(name = "RecycleBinUUID")
	@XmlJavaTypeAdapter(UUIDXmlAdapter.class)
	private UUID recycleBinUuid;

	@XmlElement(name = "RecycleBinChanged")
	private Calendar recycleBinChanged;

	@XmlElement(name = "RecycleBinEnabled")
	@XmlJavaTypeAdapter(BooleanXmlAdapter.class)
	private Boolean recycleBinEnabled;

	@XmlElement(name = "HistoryMaxItems")
	private long historyMaxItems;

	@XmlElement(name = "HistoryMaxSize")
	private long historyMaxSize;

	@XmlElement(name = "CustomIcons")
	private CustomIcons customIcons;

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
		return true;
	}

	@Override
	public String toString() {
		return "Meta [generator=" + generator + ", databaseName=" + databaseName + ", databaseDescription="
				+ databaseDescription + "]";
	}
}
