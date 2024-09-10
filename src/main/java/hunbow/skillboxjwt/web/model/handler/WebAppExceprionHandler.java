package hunbow.skillboxjwt.web.model.handler;


import hunbow.skillboxjwt.exception.AlreadyExistsException;
import hunbow.skillboxjwt.exception.EntityNotFoundException;
import hunbow.skillboxjwt.exception.RefreshTokenException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.channels.AlreadyBoundException;

@RestControllerAdvice
public class WebAppExceprionHandler {

    private final WebRequest webRequest;

    public WebAppExceprionHandler(@Qualifier("webRequest") WebRequest webRequest) {
        this.webRequest = webRequest;
    }

    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResposeBody> refreshTokenExceptionHandler(RefreshTokenException ex, WebRequest request) {
        return buildRequerst(HttpStatus.FORBIDDEN, ex, webRequest);
    }


    @ExceptionHandler(value = AlreadyBoundException.class)
    public ResponseEntity<ErrorResposeBody> alreadyExistsHandler(AlreadyExistsException ex, WebRequest request) {
        return buildRequerst(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResposeBody> notFoundHandler(EntityNotFoundException ex, WebRequest request) {
        return buildRequerst(HttpStatus.NOT_FOUND, ex, request);
    }

    private ResponseEntity<ErrorResposeBody> buildRequerst(HttpStatus httpStatus,
                                                           Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorResposeBody.builder()
                        .message(ex.getMessage())
                        .description(webRequest.getDescription(false))
                        .build());
    }


}
