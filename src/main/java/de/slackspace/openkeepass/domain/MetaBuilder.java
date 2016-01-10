package de.slackspace.openkeepass.domain;

import java.util.Calendar;
import java.util.UUID;

/**
 * A builder to create {@link Meta} objects.
 *
 */
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
	
	/**
	 * Creates a new builder with the given database name.
	 * 
	 * @param databaseName the name which should be used
	 */
	public MetaBuilder(String databaseName) {
		this.databaseName = databaseName;
	}
	
	/**
	 * Initializes the builder with values from the given meta.
	 * 
	 * @param meta the values from this will initialize the builder
	 */
	public MetaBuilder(Meta meta) {
		this.generator = meta.getGenerator();
		this.databaseName = meta.getDatabaseName();
		this.databaseDescription = meta.getDatabaseDescription();
		this.databaseNameChanged = meta.getDatabaseNameChanged();
		this.databaseDescriptionChanged = meta.getDatabaseDescriptionChanged();
		this.maintenanceHistoryDays = meta.getMaintenanceHistoryDays();
		this.recycleBinUuid = meta.getRecycleBinUuid();
		this.recycleBinEnabled = meta.getRecycleBinEnabled();
		this.recycleBinChanged = meta.getRecycleBinChanged();
		this.historyMaxItems = meta.getHistoryMaxItems();
		this.historyMaxSize = meta.getHistoryMaxSize();
	}
	
	public MetaBuilder databaseName(String databaseName) {
		this.databaseName = databaseName;
		return this;
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
	
	/**
	 * Builds a new meta with the values from the builder.
	 * 
	 * @return a new meta
	 */
	public Meta build() {
		return new Meta(this);
	}
	
}
