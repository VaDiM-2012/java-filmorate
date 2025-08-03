package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationException_returnsBadRequestWithErrorMessage() {
        ValidationException exception = new ValidationException("Invalid input data");

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Invalid input data", response.getError());
    }

    @Test
    void handleMethodArgumentNotValidException_returnsBadRequestWithErrorMessage() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", "Validation failed"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Validation failed", response.getError());
    }

    @Test
    void handleNotFoundException_returnsNotFoundWithErrorMessage() {
        NotFoundException exception = new NotFoundException("Resource not found");

        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals("Resource not found", response.getError());
    }

    @Test
    void handleServerException_returnsInternalServerErrorWithGenericMessage() {
        Exception exception = new RuntimeException("Unexpected error");

        ErrorResponse response = errorHandler.handleServerException(exception);

        assertNotNull(response);
        assertEquals("Внутренняя ошибка сервера", response.getError());
    }
}