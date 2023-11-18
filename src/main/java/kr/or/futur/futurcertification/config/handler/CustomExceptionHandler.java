package kr.or.futur.futurcertification.config.handler;

import kr.or.futur.futurcertification.domain.dto.response.ErrorResponseDTO;
import kr.or.futur.futurcertification.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler({BindException.class})
    public ResponseEntity<ErrorResponseDTO> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .msg(errorMessage)
                        .build());
    }

    @ExceptionHandler({
            DuplicateUserIdException.class,
            LoginFailedException.class,
            CertificationCodeExpiredException.class,
            DuplicatePhoneNumberException.class,
            CertificationCodeNotRequestedException.class,
            CertificationCodeMismatchException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleClientException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .msg(e.getMessage())
                        .build());
    }

    @ExceptionHandler({
            CertificationCodeSendingFailedException.class,
            SMSSendingFailedException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleServerException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.builder()
                        .msg(e.getMessage())
                        .build());
    }
}
