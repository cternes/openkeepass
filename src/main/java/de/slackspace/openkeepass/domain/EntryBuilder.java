package de.slackspace.openkeepass.domain;

import java.util.UUID;

public class EntryBuilder {

	UUID uuid;

	History history;

	String title;

	String username;

	String password;

	String notes;

	String url;

	Entry originalEntry;

	public EntryBuilder() {
		this.uuid = UUID.randomUUID();
	}

	public EntryBuilder(Entry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Parameter entry must not be null");
		}
		this.originalEntry = entry;
		this.uuid = entry.getUuid();
		this.title = entry.getTitle();
		this.history = entry.getHistory();
		this.username = entry.getUsername();
		this.password = entry.getPassword();
		this.notes = entry.getNotes();
		this.url = entry.getUrl();
	}

	public EntryBuilder(UUID uuid) {
		this.uuid = uuid;
	}

	public EntryBuilder(String title) {
		this();
		this.title = title;
	}

	public EntryBuilder title(String title) {
		this.title = title;
		return this;
	}

	public EntryBuilder uuid(UUID uuid) {
		this.uuid = uuid;
		return this;
	}

	public EntryBuilder username(String username) {
		this.username = username;
		return this;
	}

	public EntryBuilder password(String password) {
		this.password = password;
		return this;
	}

	public EntryBuilder notes(String notes) {
		this.notes = notes;
		return this;
	}

	public EntryBuilder history(History history) {
		this.history = history;
		return this;
	}

	public EntryBuilder url(String url) {
		this.url = url;
		return this;
	}

	public Entry build() {
		return new Entry(this);
	}

	/**
	 * Build a new Entry and place the original one in the history list.
	 *
	 * @return the new entry.
	 */
	public Entry buildWithHistory() {
		if (originalEntry == null) {
			throw new IllegalArgumentException("originalEntry is not set");
		}
		
		if (history == null) {
			history = new History();
		}
		
		history.getHistoricEntries().add(originalEntry);
		return build();
	}
}
