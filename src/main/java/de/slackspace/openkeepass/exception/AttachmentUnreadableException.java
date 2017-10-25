package de.slackspace.openkeepass.exception;

public class AttachmentUnreadableException extends RuntimeException {

    public AttachmentUnreadableException(String message) {
        super(message);
    }

    public AttachmentUnreadableException(String message, Throwable e) {
        super(message, e);
    }

}
