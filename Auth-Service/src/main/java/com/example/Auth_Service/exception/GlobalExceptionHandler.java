package com.example.Auth_Service.exception;

import com.example.Auth_Service.dto.ErrorResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.resolve(ex.getStatus());
		if (status == null) status = HttpStatus.BAD_REQUEST;
		return build(status, ex.getMessage(), req);
	}

	@ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<ErrorResponse> handleExternal(ExternalServiceException ex, HttpServletRequest req) {
		log.error("External service error: {}", ex.getMessage(), ex);
		return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req);
	}

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponse> handleFeign(FeignException ex, HttpServletRequest req) {
		log.error("Feign error: status={} message={}", ex.status(), ex.getMessage());
		String message =
			switch (ex.status()) {
				case 404 -> "Downstream resource not found";
				case 503 -> "Downstream service unavailable";
				default -> "Downstream service error";
			};
		HttpStatus status = HttpStatus.resolve(ex.status()) != null
			? HttpStatus.valueOf(ex.status())
			: HttpStatus.BAD_GATEWAY;
		return build(status, message, req);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
		return build(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason(), req);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		String message = ex
			.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(e -> e.getField() + ": " + e.getDefaultMessage())
			.collect(Collectors.joining("; "));
		return build(HttpStatus.BAD_REQUEST, message, req);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
		log.error("Unhandled exception at {}", req.getRequestURI(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
		ErrorResponse body = new ErrorResponse(
			Instant.now(),
			status.value(),
			status.getReasonPhrase(),
			message,
			req.getRequestURI()
		);
		return ResponseEntity.status(status).body(body);
	}
}
