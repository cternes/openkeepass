package de.slackspace.openkeepass.exception;

public class KeePassHeaderUnreadableException extends RuntimeException {

    public KeePassHeaderUnreadableException(String message) {
        super(message);
    }

    public KeePassHeaderUnreadableException(String message, Throwable e) {
        super(message, e);
    }
}
