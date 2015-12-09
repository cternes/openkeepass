package de.slackspace.openkeepass.domain;

import java.util.Calendar;
import java.util.UUID;

public class MetaBuilder {

	String generator = "KeePass";
	
	String databaseName;
	
	String databaseDescription;
	
	Calendar databaseNameChanged;

	Calendar databaseDescriptionChanged;
	
	int maintenanceHistoryDays;
	
	UUID recycleBinUuid; 
	
	Calendar recycleBinChanged;
	
	boolean recycleBinEnabled;
	
	long historyMaxItems;
	
	long historyMaxSize;
	
	public MetaBuilder(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public MetaBuilder generator(String generator) {
		this.generator = generator;
		return this;
	}
	
	public MetaBuilder databaseDescription(String databaseDescription) {
		this.databaseDescription = databaseDescription;
		return this;
	}
	
	public MetaBuilder databaseNameChanged(Calendar databaseNameChanged) {
		this.databaseNameChanged = databaseNameChanged;
		return this;
	}
	
	public MetaBuilder databaseDescriptionChanged(Calendar databaseDescriptionChanged) {
		this.databaseDescriptionChanged = databaseDescriptionChanged;
		return this;
	}
	
	public MetaBuilder maintenanceHistoryDays(int maintenanceHistoryDays) {
		this.maintenanceHistoryDays = maintenanceHistoryDays;
		return this;
	}

	public MetaBuilder recycleBinUuid(UUID recycleBinUuid) {
		this.recycleBinUuid = recycleBinUuid;
		return this;
	}
	
	public MetaBuilder recycleBinChanged(Calendar recycleBinChanged) {
		this.recycleBinChanged = recycleBinChanged;
		return this;
	}
	
	public MetaBuilder recycleBinEnabled(boolean recycleBinEnabled) {
		this.recycleBinEnabled = recycleBinEnabled;
		return this;
	}
	
	public MetaBuilder historyMaxItems(long historyMaxItems) {
		this.historyMaxItems = historyMaxItems;
		return this;
	}
	
	public MetaBuilder historyMaxSize(long historyMaxSize) {
		this.historyMaxSize = historyMaxSize;
		return this;
	}
	
	public Meta build() {
		return new Meta(this);
	}
	
}
