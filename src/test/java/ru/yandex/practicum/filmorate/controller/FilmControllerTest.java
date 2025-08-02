package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
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
        assertEquals(1, addedFilm.getId());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void addFilm_invalidReleaseDate_throwsValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
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

    @Test
    void getFilmById_validId() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);

        Film result = filmController.getFilmById(1);
        assertNotNull(result);
        assertEquals("Test Film", result.getName());
    }

    @Test
    void getFilmById_invalidId_throwsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.getFilmById(999));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void addLike_validFilmAndUser() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmController.addLike(1, 1);

        Film updatedFilm = filmController.getFilmById(1);
        assertNotNull(updatedFilm.getLikes());
        assertTrue(updatedFilm.getLikes().contains(1));
        assertEquals(1, updatedFilm.getLikes().size());
    }

    @Test
    void addLike_invalidFilmId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.addLike(999, 1));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void addLike_invalidUserId_throwsNotFoundException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.addLike(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeLike_validFilmAndUser() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmController.addLike(1, 1);
        filmController.removeLike(1, 1);

        Film updatedFilm = filmController.getFilmById(1);
        assertNotNull(updatedFilm.getLikes());
        assertFalse(updatedFilm.getLikes().contains(1));
        assertEquals(0, updatedFilm.getLikes().size());
    }

    @Test
    void removeLike_invalidFilmId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.removeLike(999, 1));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeLike_invalidUserId_throwsNotFoundException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.removeLike(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getPopularFilms_sortedByLikes() {
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

        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("testuser1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("testuser2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.addUser(user2);

        filmController.addLike(1, 1); // Film 1: 1 лайк
        filmController.addLike(2, 1); // Film 2: 1 лайк
        filmController.addLike(1, 2); // Film 1: 2 лайка

        List<Film> popularFilms = filmController.getPopularFilms(2);
        assertEquals(2, popularFilms.size());
        assertEquals("Film 1", popularFilms.get(0).getName()); // Film 1 должен быть первым (2 лайка)
        assertEquals("Film 2", popularFilms.get(1).getName()); // Film 2 второй (1 лайк)
    }
}