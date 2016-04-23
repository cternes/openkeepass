package de.slackspace.openkeepass.exception;

public class KeePassDatabaseUnreadableException extends RuntimeException {

    public KeePassDatabaseUnreadableException(String message) {
        super(message);
    }

    public KeePassDatabaseUnreadableException(String message, Throwable e) {
        super(message, e);
    }

}
