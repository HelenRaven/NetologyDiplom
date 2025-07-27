package org.example.diplom.exception;

import org.example.diplom.entity.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(NotFoundData.class)
    public ResponseEntity<ErrorMessage> invalidInputDataHandler(NotFoundData e){
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ErrorMessage> invalidInputDataHandler(Unauthorized e){
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentials.class)
    public ResponseEntity<ErrorMessage> invalidInputDataHandler(BadCredentials e){
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerError.class)
    public ResponseEntity<ErrorMessage> invalidInputDataHandler(ServerError e){
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorMessage> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return new ResponseEntity<>(new ErrorMessage("File too large!"), HttpStatus.EXPECTATION_FAILED);
    }
}
