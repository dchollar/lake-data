package com.lake.controller

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException
import javax.persistence.OptimisticLockException
import javax.xml.bind.ValidationException

@CompileStatic
@Slf4j
@RestControllerAdvice
class ErrorController {

    @ExceptionHandler([EntityNotFoundException, NoResultException])
    ResponseEntity handleDataNotFound(Exception e) {
        log.debug('NOT FOUND', e)
        return new ResponseEntity(createBody(e), HttpStatus.NO_CONTENT)
    }

    @ExceptionHandler([EntityExistsException, DataIntegrityViolationException, OptimisticLockException])
    ResponseEntity handleDataConflict(Exception e) {
        log.error('DATA CONFLICT', e)
        return new ResponseEntity(createBody(e), HttpStatus.CONFLICT)
    }

    @ExceptionHandler([ValidationException, IllegalArgumentException])
    ResponseEntity handleBadRequests(Exception e) {
        log.info('Validation issue: ' + ExceptionUtils.getRootCauseMessage(e))
        return new ResponseEntity(createBody(e), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception)
    ResponseEntity handleUnknownException(Exception e) {
        log.error('Something really bad happened', e)
        return new ResponseEntity(createBody(e), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private static Map<String, Object> createBody(Exception e) {
        Map<String, Object> message = [:]
        message.put('errorMessage', ExceptionUtils.getRootCauseMessage(e))
        message
    }

}
