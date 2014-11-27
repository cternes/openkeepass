package de.slackspace.openkeepass.domain;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
	private String recycleBinUuid; 
	
	@XmlElement(name = "RecycleBinChanged")
	private Calendar recycleBinChanged;
	
	@XmlElement(name = "RecycleBinEnabled")
	private String recycleBinEnabled;
	
	@XmlElement(name = "HistoryMaxItems")
	private long historyMaxItems;
	
	@XmlElement(name = "HistoryMaxSize")
	private long historyMaxSize;

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseDescription() {
		return databaseDescription;
	}

	public void setDatabaseDescription(String databaseDescription) {
		this.databaseDescription = databaseDescription;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public Calendar getDatabaseNameChanged() {
		return databaseNameChanged;
	}

	public void setDatabaseNameChanged(Calendar databaseNameChanged) {
		this.databaseNameChanged = databaseNameChanged;
	}

	public Calendar getDatabaseDescriptionChanged() {
		return databaseDescriptionChanged;
	}

	public void setDatabaseDescriptionChanged(Calendar databaseDescriptionChanged) {
		this.databaseDescriptionChanged = databaseDescriptionChanged;
	}

	public int getMaintenanceHistoryDays() {
		return maintenanceHistoryDays;
	}

	public void setMaintenanceHistoryDays(int maintenanceHistoryDays) {
		this.maintenanceHistoryDays = maintenanceHistoryDays;
	}

	public String getRecycleBinUuid() {
		return recycleBinUuid;
	}

	public void setRecycleBinUuid(String recycleBinUuid) {
		this.recycleBinUuid = recycleBinUuid;
	}

	public Calendar getRecycleBinChanged() {
		return recycleBinChanged;
	}

	public void setRecycleBinChanged(Calendar recycleBinChanged) {
		this.recycleBinChanged = recycleBinChanged;
	}

	public boolean getRecycleBinEnabled() {
		if(recycleBinEnabled == null) {
			return false;
		}
		
		return recycleBinEnabled.equalsIgnoreCase("true");
	}

	public void setRecycleBinEnabled(boolean recycleBinEnabled) {
		this.recycleBinEnabled = recycleBinEnabled == true ? "True" : "False";
	}

	public long getHistoryMaxItems() {
		return historyMaxItems;
	}

	public void setHistoryMaxItems(long historyMaxItems) {
		this.historyMaxItems = historyMaxItems;
	}

	public long getHistoryMaxSize() {
		return historyMaxSize;
	}

	public void setHistoryMaxSize(long historyMaxSize) {
		this.historyMaxSize = historyMaxSize;
	}
}
