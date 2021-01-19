package kp.backend.dataService.exception;

public class KPBusinessException extends RuntimeException {

    public KPBusinessException() {
        super();
    }

    public KPBusinessException(String message) {
        super(message);
    }

    public KPBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public KPBusinessException(Throwable cause) {
        super(cause);
    }

    protected KPBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
