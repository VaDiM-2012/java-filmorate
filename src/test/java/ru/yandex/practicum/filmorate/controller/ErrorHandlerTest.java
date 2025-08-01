package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationException_returnsBadRequestWithErrorMessage() {
        ValidationException exception = new ValidationException("Invalid input data");

        Map<String, String> response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Invalid input data", response.get("error"));
    }

    @Test
    void handleMethodArgumentNotValidException_returnsBadRequestWithErrorMessage() {
        // Создаем BindingResult
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", "Validation failed"));

        // Создаем MethodParameter
        Method method;
        try {
            method = FilmController.class.getMethod("addFilm", Film.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find method for MethodParameter", e);
        }
        MethodParameter methodParameter = new MethodParameter(method, 0);

        // Создаем исключение
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        Map<String, String> response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Validation failed", response.get("error"));
    }

    @Test
    void handleNotFoundException_returnsNotFoundWithErrorMessage() {
        NotFoundException exception = new NotFoundException("Resource not found");

        Map<String, String> response = errorHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals("Resource not found", response.get("error"));
    }

    @Test
    void handleServerException_returnsInternalServerErrorWithGenericMessage() {
        Exception exception = new RuntimeException("Unexpected error");

        Map<String, String> response = errorHandler.handleServerException(exception);

        assertNotNull(response);
        assertEquals("Внутренняя ошибка сервера", response.get("error"));
    }
}