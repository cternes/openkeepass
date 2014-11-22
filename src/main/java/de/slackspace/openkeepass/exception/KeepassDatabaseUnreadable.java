package de.slackspace.openkeepass.exception;

public class KeepassDatabaseUnreadable extends RuntimeException {

	public KeepassDatabaseUnreadable(String message) {
		super(message);
	}
	
	public KeepassDatabaseUnreadable(String message, Throwable e) {
		super(message, e);
	}

}
