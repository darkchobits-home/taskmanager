package com.sebastien.taskmanager.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status; // HTTP status code of the error (e.g., 404, 500).

    private String error; // Description of the HTTP status (e.g., "Not Found", "Internal Server Error").

    private int code; // Code of the exception

    private String message; // User-friendly error message.

    private String path; // The URL of the request that generated the error.

    private String internalCode; // Internal error code for the developer (optional).

    private Map<String, String> details; // Additional error details (optional).

    public ErrorResponse(HttpStatus status, int code, String message, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(HttpStatus status, int code, String message, String path, String internalCode) {
        this(status, code, message, path);
        this.internalCode = internalCode;
    }

    public ErrorResponse(HttpStatus status, int code, String message, String path, Map<String, String> details) {
        this(status, code, message, path);
        this.details = details;
    }
}
