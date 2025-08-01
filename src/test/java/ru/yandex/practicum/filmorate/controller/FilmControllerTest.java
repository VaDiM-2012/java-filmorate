package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void addFilm_addValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A test film description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals("Test Film", addedFilm.getName());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void updateFilm_updateValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);

        Film result = filmController.updateFilm(updatedFilm);
        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(LocalDate.of(2001, 1, 1), result.getReleaseDate());
        assertEquals(150, result.getDuration());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void updateFilm_failUpdateNonExistentFilm() {
        Film film = new Film();
        film.setId(999);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        assertEquals("Фильм не найден", exception.getMessage());
    }

    @Test
    void getAllFilms_getAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(150);
        filmController.addFilm(film2);

        assertEquals(2, filmController.getAllFilms().size());
    }
}