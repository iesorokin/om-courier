package ru.iesorokin.ordermanager.courier.input.handler

import mu.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.iesorokin.ordermanager.courier.error.ErrorCode
import ru.iesorokin.ordermanager.courier.output.handler.ClientResponseErrorException
import java.util.*

private val log = KotlinLogging.logger { }

@RestControllerAdvice
class RestErrorHandler(val messageSource: MessageSource) : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [Throwable::class])
    fun handle(e: Throwable): ResponseEntity<Any> {
        log.error(e.message, e)
        return response(
                errors = listOf(buildErrorDto(ErrorCode.UNEXPECTED)),
                status = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(value = [ClientResponseErrorException::class])
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handle(e: ClientResponseErrorException): ErrorResponse {
        log.error(e.message, e)
        return e.errorCode.toErrorResponse()
    }

    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException?, headers: HttpHeaders?,
                                              status: HttpStatus?, request: WebRequest?): ResponseEntity<Any> {
        val errors = ex?.bindingResult?.fieldErrors
                ?.map {
                    buildErrorDto(ErrorCode.INVALID_ATTRIBUTE, it.field, it.defaultMessage)
                }.orEmpty()
        return response(
                errors = errors,
                status = HttpStatus.BAD_REQUEST
        )
    }

    fun buildErrorDto(error: ErrorCode, vararg args: String?): ErrorDescription {
        return ErrorDescription(error.code, messageSource.getMessage(error.errorMessage, args, Locale.getDefault()))
    }

    fun response(errors: List<ErrorDescription>, status: HttpStatus): ResponseEntity<Any> {
        log.info { errors }
        return ResponseEntity(ErrorResponse(errors), status)
    }

    private fun ErrorCode.toErrorResponse() = ErrorResponse(listOf(ErrorDescription(code, errorMessage)))

}

data class ErrorResponse(val errors: Collection<ErrorDescription>)

data class ErrorDescription(val code: Int,
                            val message: String)

