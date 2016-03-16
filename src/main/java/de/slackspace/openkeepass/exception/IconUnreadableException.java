package de.slackspace.openkeepass.exception;

public class IconUnreadableException extends RuntimeException {

    public IconUnreadableException(String message) {
        super(message);
    }

    public IconUnreadableException(String message, Throwable e) {
        super(message, e);
    }

}
