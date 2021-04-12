package org.oddjob.resolve;

public class ResolveException extends RuntimeException {

    public ResolveException() {
        super();
    }

    public ResolveException(String message) {
        super(message);
    }

    public ResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolveException(Throwable cause) {
        super(cause);
    }

}
