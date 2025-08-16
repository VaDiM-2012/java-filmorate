package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmService.class, FilmDbStorage.class, UserDbStorage.class, LikeDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
class FilmServiceTest {

    private final FilmService filmService;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Autowired
    FilmServiceTest(FilmService filmService, FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Test
    void addLike_validFilmAndUser_addsLike() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmService.addLike(film.getId(), user.getId());

        Film updatedFilm = filmStorage.getFilmById(film.getId()).orElseThrow();
        assertEquals(1, updatedFilm.getRate(), "Rate should be 1 after adding a like");
    }

    @Test
    void addLike_invalidFilmId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(999, user.getId()));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void addLike_invalidUserId_throwsNotFoundException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmStorage.addFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(film.getId(), 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeLike_validFilmAndUser_removesLike() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmService.addLike(film.getId(), user.getId());
        filmService.removeLike(film.getId(), user.getId());

        Film updatedFilm = filmStorage.getFilmById(film.getId()).orElseThrow();
        assertEquals(0, updatedFilm.getRate(), "Rate should be 0 after removing the like");
    }

    @Test
    void removeLike_invalidFilmId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(999, user.getId()));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeLike_invalidUserId_throwsNotFoundException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmStorage.addFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(film.getId(), 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getPopularFilms_returnsFilmsSortedByLikes() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        film1.setMpa(mpa1);
        filmStorage.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(150);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);
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

        filmService.addLike(film1.getId(), user1.getId()); // Film 1: 1 like
        filmService.addLike(film1.getId(), user2.getId()); // Film 1: 2 likes
        filmService.addLike(film2.getId(), user1.getId()); // Film 2: 1 like

        List<Film> popularFilms = filmService.getPopularFilms(2);
        assertEquals(2, popularFilms.size());
        assertEquals("Film 1", popularFilms.get(0).getName(), "Film 1 should be first (2 likes)");
        assertEquals("Film 2", popularFilms.get(1).getName(), "Film 2 should be second (1 like)");
    }

    @Test
    void getPopularFilms_emptyListWhenNoFilms() {
        List<Film> popularFilms = filmService.getPopularFilms(10);
        assertTrue(popularFilms.isEmpty(), "List should be empty when no films exist");
    }
}