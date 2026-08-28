package com.erikjarquin.ventas.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CashException.class)
    public ResponseEntity<ErrorResponse> handleCashException(CashException ex){
        ErrorResponse response = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserException ex){
        ErrorResponse response = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage()
        );
    
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(SaleException.class)
    public ResponseEntity<ErrorResponse> handleSaleException(SaleException e){
        log.warn("Error de venta: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "SALE_ERROR", e.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e){
        log.warn("Error de pago: {}", e.getMessage());

        //Diferentes códigos según el tipo de error
        if(e.getMessage().contains("timeout") || e.getMessage().contains("TIMEOUT")){
            return buildErrorResponse(HttpStatus.REQUEST_TIMEOUT, "PAYMENT_TIMEOUT", e.getMessage());
        }

        if(e.getMessage().contains("comunicación") || e.getMessage().contains("terminal")){
            return buildErrorResponse(HttpStatus.BAD_GATEWAY, "TERMINAL_ERROR", e.getMessage());
        }

        if(e.getMessage().contains("rechazado")){
            return buildErrorResponse(HttpStatus.PAYMENT_REQUIRED, "PAYMENT_REJECTED", e.getMessage());
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "PAYMENT_ERROR", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e){
        log.error("Error interno del servidor", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocurrió un error interno en el servidor");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String code, String message){
        ErrorResponse error = new ErrorResponse(LocalDateTime. now(), status.value(), code, message);

        return ResponseEntity.status(status).body(error);
    }

    //Clase interna para respuesta de error
    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String code;
        private final String message;

        public ErrorResponse(LocalDateTime timestamp, int status, String code, String message){
            this.timestamp=timestamp;
            this.status=status;
            this.code=code;
            this.message=message;
        }

        //Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getCode(){ return code; }
        public String getMessage() { return message; }
    }
}


        
