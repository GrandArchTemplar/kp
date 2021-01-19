package src.kp.backend.apiGateway.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception to handle RestTemplates errors
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Remote server responded with an error")
public class HTTPRequestException extends RuntimeException {

    @Getter
    @Setter
    private HttpStatus httpStatus;

    public HTTPRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HTTPRequestException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HTTPRequestException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public HTTPRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus httpStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.httpStatus = httpStatus;
    }

}
