package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    void addFilm_validFilm_addsFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmStorage.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals(film.getName(), addedFilm.getName());
        assertEquals(1, filmStorage.getAllFilms().size());
    }

    @Test
    void updateFilm_validFilm_updatesFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(film.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(130);

        Film result = filmStorage.updateFilm(updatedFilm);

        assertEquals(updatedFilm.getName(), result.getName());
        assertEquals(updatedFilm.getDescription(), result.getDescription());
        assertEquals(1, filmStorage.getAllFilms().size());
    }

    @Test
    void deleteFilm_validId_deletesFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        filmStorage.deleteFilm(film.getId());

        assertTrue(filmStorage.getAllFilms().isEmpty());
    }

    @Test
    void getFilmById_validId_returnsFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        Optional<Film> result = filmStorage.getFilmById(film.getId());

        assertTrue(result.isPresent());
        assertEquals(film.getName(), result.get().getName());
    }

    @Test
    void getFilmById_invalidId_returnsEmpty() {
        Optional<Film> result = filmStorage.getFilmById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllFilms_returnsAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        filmStorage.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        filmStorage.addFilm(film2);

        List<Film> films = filmStorage.getAllFilms();

        assertEquals(2, films.size());
        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
    }
}