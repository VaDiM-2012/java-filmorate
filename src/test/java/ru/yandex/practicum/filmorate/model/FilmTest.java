package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private final Validator validator;
    private final InMemoryFilmStorage filmStorage;

    public FilmTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    void film_validFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A test film description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Валидный фильм не должен иметь нарушений");
    }

    @Test
    void film_emptyName_validationFails() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
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
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Название не должно быть null");
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void film_tooLongDescription_validationFails() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Описание не должно превышать 200 символов");
        assertEquals("Описание не может превышать 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void film_maxDescriptionLength_validationPasses() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Описание длиной 200 символов разрешено");
    }

    @Test
    void film_nullReleaseDate_validationFails() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Дата релиза не должна быть null");
        assertEquals("Дата релиза не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void film_earlyReleaseDate_throwsValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmStorage.addFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void film_boundaryReleaseDate_validationPasses() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);
        assertDoesNotThrow(() -> filmStorage.addFilm(film), "Граничная дата релиза разрешена");
        assertEquals(LocalDate.of(1895, 12, 28), filmStorage.getFilmById(1).get().getReleaseDate());
    }

    @Test
    void film_nonPositiveDuration_validationFails() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Продолжительность должна быть положительной");
        assertEquals("Продолжительность фильма должна быть положительной", violations.iterator().next().getMessage());

        film.setDuration(-10);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Продолжительность должна быть положительной");
        assertEquals("Продолжительность фильма должна быть положительной", violations.iterator().next().getMessage());
    }
}