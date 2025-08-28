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
}