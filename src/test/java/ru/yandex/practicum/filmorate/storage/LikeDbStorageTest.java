package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({LikeDbStorage.class, FilmDbStorage.class, UserDbStorage.class})
class LikeDbStorageTest {

    private final LikeDbStorage likeStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Autowired
    LikeDbStorageTest(LikeDbStorage likeStorage, FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.likeStorage = likeStorage;
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

        likeStorage.addLike(film.getId(), user.getId());

        Film updatedFilm = filmStorage.getFilmById(film.getId()).get();
        assertEquals(1, updatedFilm.getRate());
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

        likeStorage.addLike(film.getId(), user.getId());
        likeStorage.removeLike(film.getId(), user.getId());

        Film updatedFilm = filmStorage.getFilmById(film.getId()).get();
        assertEquals(0, updatedFilm.getRate());
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
        film2.setDuration(130);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);
        filmStorage.addFilm(film2);

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

        likeStorage.addLike(film1.getId(), user1.getId());
        likeStorage.addLike(film1.getId(), user2.getId());
        likeStorage.addLike(film2.getId(), user1.getId());

        List<Film> popularFilms = likeStorage.getPopularFilms(2);

        assertEquals(2, popularFilms.size());
        assertEquals("Film 1", popularFilms.get(0).getName());
        assertEquals("Film 2", popularFilms.get(1).getName());
    }
}