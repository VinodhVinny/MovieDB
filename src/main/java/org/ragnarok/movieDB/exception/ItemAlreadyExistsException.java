package org.ragnarok.movieDB.exception;

public class ItemAlreadyExistsException extends Exception {

    public ItemAlreadyExistsException(String message) {
        super(message);
    }

    public ItemAlreadyExistsException() {
        super();
    }

    public ItemAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected ItemAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
