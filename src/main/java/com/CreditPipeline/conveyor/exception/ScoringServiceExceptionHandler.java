package com.creditPipeline.conveyor.exception;

import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ScoringServiceExceptionHandler {

    @ExceptionHandler(value = {ScoringServiceException.class})
    public ResponseEntity<Object> handleScoringServiceException(ScoringServiceException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        return new ResponseEntity<>(scoringDataDTO,badRequest);
    }

    @ExceptionHandler(value = {AgeException.class})
    public ResponseEntity<Object> handleAgeException(AgeException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(e.getMessage(),badRequest);
    }

}
