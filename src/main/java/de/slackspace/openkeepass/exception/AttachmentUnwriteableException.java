package de.slackspace.openkeepass.exception;

public class AttachmentUnwriteableException extends RuntimeException {

    public AttachmentUnwriteableException(String message) {
        super(message);
    }

    public AttachmentUnwriteableException(String message, Throwable e) {
        super(message, e);
    }

}
