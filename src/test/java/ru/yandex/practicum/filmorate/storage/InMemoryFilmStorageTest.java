package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
    void addFilm_validFilm_assignsIdAndStores() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmStorage.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Test Film", addedFilm.getName());
        assertEquals(1, filmStorage.getAllFilms().size());
    }

    @Test
    void addFilm_invalidReleaseDate_throwsValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmStorage.addFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void updateFilm_validFilm_updatesExistingFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);

        Film result = filmStorage.updateFilm(updatedFilm);
        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(LocalDate.of(2001, 1, 1), result.getReleaseDate());
        assertEquals(150, result.getDuration());
        assertEquals(1, filmStorage.getAllFilms().size());
    }

    @Test
    void updateFilm_invalidFilmId_throwsNotFoundException() {
        Film film = new Film();
        film.setId(999);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(film));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void updateFilm_invalidReleaseDate_throwsValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        updatedFilm.setDuration(150);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmStorage.updateFilm(updatedFilm));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void deleteFilm_validId_removesFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        filmStorage.deleteFilm(1);
        assertTrue(filmStorage.getAllFilms().isEmpty());
    }

    @Test
    void deleteFilm_invalidId_throwsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmStorage.deleteFilm(999));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void getFilmById_validId_returnsFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        Optional<Film> result = filmStorage.getFilmById(1);
        assertTrue(result.isPresent());
        assertEquals("Test Film", result.get().getName());
    }

    @Test
    void getFilmById_invalidId_returnsEmptyOptional() {
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
        film2.setDuration(150);
        filmStorage.addFilm(film2);

        List<Film> films = filmStorage.getAllFilms();
        assertEquals(2, films.size());
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("Film 1")));
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("Film 2")));
    }

    @Test
    void getAllFilms_emptyStorage_returnsEmptyList() {
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.isEmpty());
    }
}