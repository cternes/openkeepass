package de.slackspace.openkeepass.exception;

public class KeePassDatabaseUnwriteable extends RuntimeException {

	public KeePassDatabaseUnwriteable(String message) {
		super(message);
	}
	
	public KeePassDatabaseUnwriteable(String message, Throwable e) {
		super(message, e);
	}
}
