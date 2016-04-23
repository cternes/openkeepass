package de.slackspace.openkeepass.exception;

public class KeyFileUnreadableException extends RuntimeException {

    public KeyFileUnreadableException(String message) {
        super(message);
    }

    public KeyFileUnreadableException(String message, Throwable e) {
        super(message, e);
    }

}
