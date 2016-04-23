package de.slackspace.openkeepass.exception;

public class KeePassDatabaseUnwriteableException extends RuntimeException {

    public KeePassDatabaseUnwriteableException(String message) {
        super(message);
    }

    public KeePassDatabaseUnwriteableException(String message, Throwable e) {
        super(message, e);
    }
}
