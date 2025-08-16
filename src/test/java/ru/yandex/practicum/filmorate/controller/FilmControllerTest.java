package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmController.class, FilmService.class, FilmDbStorage.class, UserDbStorage.class, LikeDbStorage.class,
        MpaDbStorage.class, GenreDbStorage.class})
class FilmControllerTest {

    private final FilmController filmController;
    private final UserDbStorage userStorage;

    @Autowired
    FilmControllerTest(FilmController filmController, UserDbStorage userStorage) {
        this.filmController = filmController;
        this.userStorage = userStorage;
    }

    @Test
    void addFilm_validFilm_addsFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals("Test Film", addedFilm.getName());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void addFilm_invalidReleaseDate_throwsValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void updateFilm_validFilm_updatesFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmController.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(film.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);
        updatedFilm.setMpa(mpa);

        Film result = filmController.updateFilm(updatedFilm);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void updateFilm_invalidId_throwsNotFoundException() {
        Film film = new Film();
        film.setId(999);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void deleteFilm_validId_deletesFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmController.addFilm(film);

        filmController.deleteFilm(film.getId());

        assertEquals(0, filmController.getAllFilms().size());
    }

    @Test
    void getAllFilms_returnsAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        film1.setMpa(mpa1);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);
        filmController.addFilm(film2);

        List<Film> films = filmController.getAllFilms();

        assertEquals(2, films.size());
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("Film 1")));
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("Film 2")));
    }

    @Test
    void getFilmById_validId_returnsFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmController.addFilm(film);

        Film result = filmController.getFilmById(film.getId());

        assertEquals("Test Film", result.getName());
    }

    @Test
    void getFilmById_invalidId_throwsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.getFilmById(999));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
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
        filmController.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmController.addLike(film.getId(), user.getId());

        Film updatedFilm = filmController.getFilmById(film.getId());
        assertEquals(1, updatedFilm.getRate());
    }

    @Test
    void addLike_invalidFilmId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.addLike(999, user.getId()));
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
        filmController.addFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.addLike(film.getId(), 999));
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
        filmController.addFilm(film);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmController.addLike(film.getId(), user.getId());
        filmController.removeLike(film.getId(), user.getId());

        Film updatedFilm = filmController.getFilmById(film.getId());
        assertEquals(0, updatedFilm.getRate());
    }

    @Test
    void getPopularFilms_validFilms_returnsSortedFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        film1.setMpa(mpa1);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);
        filmController.addFilm(film2);

        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.addUser(user2);

        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film1.getId(), user2.getId());
        filmController.addLike(film2.getId(), user1.getId());

        List<Film> popularFilms = filmController.getPopularFilms(2);

        assertEquals(2, popularFilms.size());
        assertEquals("Film 1", popularFilms.get(0).getName());
        assertEquals("Film 2", popularFilms.get(1).getName());
    }
}