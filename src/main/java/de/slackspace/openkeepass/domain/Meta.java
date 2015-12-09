package de.slackspace.openkeepass.domain;

import java.util.Calendar;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.xml.BooleanXmlAdapter;
import de.slackspace.openkeepass.xml.UUIDXmlAdapter;

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

	Meta() {}
	
	public Meta(MetaBuilder metaBuilder) {
		this.databaseDescription = metaBuilder.databaseDescription;
		this.databaseDescriptionChanged = metaBuilder.databaseDescriptionChanged;
		this.databaseName = metaBuilder.databaseName;
		this.databaseNameChanged = metaBuilder.databaseNameChanged;
		this.generator = metaBuilder.generator;
		this.historyMaxItems = metaBuilder.historyMaxItems;
		this.historyMaxSize = metaBuilder.historyMaxSize;
		this.maintenanceHistoryDays = metaBuilder.maintenanceHistoryDays;
		this.recycleBinChanged = metaBuilder.recycleBinChanged;
		this.recycleBinEnabled = metaBuilder.recycleBinEnabled;
		this.recycleBinUuid = metaBuilder.recycleBinUuid;
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
}
