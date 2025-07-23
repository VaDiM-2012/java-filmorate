package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private final Validator validator;

    public FilmTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void film_validFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A test film description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Валидный фильм не должен иметь нарушений");
    }

    @Test
    void film_emptyName_validationFails() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Название не должно быть пустым");
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void film_nullName_validationFails() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Название не должно быть null");
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void film_tooLongDescription_validationFails() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Описание не должно превышать 200 символов");
        assertEquals("Описание не может превышать 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void film_maxDescriptionLength_validationPasses() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(200)); // Ровно 200 символов
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Описание длиной 200 символов разрешено");
    }

    @Test
    void film_nullReleaseDate_validationFails() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setDuration(120);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Дата релиза не должна быть null");
        assertEquals("Дата релиза не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void film_earlyReleaseDate_throwsValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setDuration(120);
        // Установка даты релиза до 28 декабря 1895 года
        LocalDate earlyDate = LocalDate.of(1895, 12, 27);
        ValidationException exception = assertThrows(ValidationException.class, () -> film.setReleaseDate(earlyDate));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void film_boundaryReleaseDate_validationPasses() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setDuration(120);
        // Установка даты релиза 28 декабря 1895 года
        LocalDate boundaryDate = LocalDate.of(1895, 12, 28);
        assertDoesNotThrow(() -> film.setReleaseDate(boundaryDate), "Граничная дата релиза разрешена");
        assertEquals(boundaryDate, film.getReleaseDate());
    }

    @Test
    void film_nonPositiveDuration_validationFails() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0); // Продолжительность 0
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Продолжительность должна быть положительной");
        assertEquals("Продолжительность фильма должна быть положительной", violations.iterator().next().getMessage());

        film.setDuration(-10); // Отрицательная продолжительность
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Продолжительность должна быть положительной");
        assertEquals("Продолжительность фильма должна быть положительной", violations.iterator().next().getMessage());
    }
}