package com.lake.controller

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException
import javax.persistence.OptimisticLockException
import javax.xml.bind.ValidationException

@Slf4j
@ControllerAdvice
class ErrorController {

    @ExceptionHandler([EntityNotFoundException, NoResultException])
    ResponseEntity handleNotFound(Exception e) {
        log.debug('NOT FOUND', e)
        return new ResponseEntity(createBody(e), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler([EntityExistsException, DataIntegrityViolationException, OptimisticLockException])
    ResponseEntity handleConflict(Exception e) {
        log.error('DATA CONFLICT', e)
        return new ResponseEntity(createBody(e), HttpStatus.CONFLICT)
    }

    @ExceptionHandler
    ResponseEntity handleBadRequests(ValidationException e) {
        log.error('BAD REQUEST', e)
        return new ResponseEntity(createBody(e), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    ResponseEntity handleBadRequests(IllegalArgumentException e) {
        log.error('Not really sure what happened here but we threw this one', e)
        return new ResponseEntity(createBody(e), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler
    ResponseEntity handleUnknownException(Exception e) {
        log.error('Something really bad happened', e)
        return new ResponseEntity(createBody(e), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String exception(final Throwable throwable, final Model model) {
        log.error("Something bad happened", throwable)
        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error")
        model.addAttribute("errorMessage", errorMessage)
        return "error"
    }

    private static Map<String, Object> createBody(Exception e) {
        Map<String, Object> message = [:]
        message.put('errorMessage', ExceptionUtils.getRootCauseMessage(e))
        message
    }

}
