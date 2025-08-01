package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }

    @Test
    void addLike_validFilmAndUser_addsLike() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmService.addLike(1, 1);

        Film updatedFilm = filmStorage.getFilmById(1).get();
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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(999, 1));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void addLike_invalidUserId_throwsNotFoundException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeLike_validFilmAndUser_removesLike() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmService.addLike(1, 1);
        filmService.removeLike(1, 1);

        Film updatedFilm = filmStorage.getFilmById(1).get();
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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(999, 1));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeLike_invalidUserId_throwsNotFoundException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        filmStorage.addFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getPopularFilms_returnsFilmsSortedByLikes() {
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

        filmService.addLike(1, 1); // Film 1: 1 лайк
        filmService.addLike(2, 1); // Film 2: 1 лайк
        filmService.addLike(1, 2); // Film 1: 2 лайка

        List<Film> popularFilms = filmService.getPopularFilms(2);
        assertEquals(2, popularFilms.size());
        assertEquals("Film 1", popularFilms.get(0).getName()); // 2 лайка
        assertEquals("Film 2", popularFilms.get(1).getName()); // 1 лайк
    }

    @Test
    void getPopularFilms_emptyListWhenNoFilms() {
        List<Film> popularFilms = filmService.getPopularFilms(10);
        assertTrue(popularFilms.isEmpty(), "Список должен быть пустым, если нет фильмов");
    }
}