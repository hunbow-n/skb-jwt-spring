package hunbow.skillboxjwt.exception;

import org.apache.logging.log4j.message.Message;

import java.text.MessageFormat;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String token, String message) {
        super(MessageFormat.format("Error trying to refresh token {0}: {1}", token, message));
    }

    public RefreshTokenException(String message) {
        super(message);
    }
}
