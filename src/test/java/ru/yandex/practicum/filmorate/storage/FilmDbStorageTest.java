package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Autowired
    FilmDbStorageTest(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Test
    void addFilm_validFilm_addsFilmWithGenresAndMpa() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1); // Устанавливаем существующий MPA ID
        film.setMpa(mpa);
        Genre genre = new Genre();
        genre.setId(1);
        film.setGenres(List.of(genre));

        Film addedFilm = filmStorage.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals("Test Film", addedFilm.getName());
        assertEquals(1, addedFilm.getMpa().getId());
        assertEquals(1, addedFilm.getGenres().size());
        assertEquals(1, addedFilm.getGenres().get(0).getId());
    }

    @Test
    void updateFilm_validFilm_updatesFilmWithGenresAndMpa() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        Genre genre = new Genre();
        genre.setId(1);
        film.setGenres(List.of(genre));
        filmStorage.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(film.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(130);
        Mpa updatedMpa = new Mpa();
        updatedMpa.setId(2);
        updatedFilm.setMpa(updatedMpa);
        Genre updatedGenre = new Genre();
        updatedGenre.setId(2);
        updatedFilm.setGenres(List.of(updatedGenre));

        Film result = filmStorage.updateFilm(updatedFilm);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(LocalDate.of(2001, 1, 1), result.getReleaseDate());
        assertEquals(130, result.getDuration());
        assertEquals(2, result.getMpa().getId());
        assertEquals(1, result.getGenres().size());
        assertEquals(2, result.getGenres().get(0).getId());
    }

    @Test
    void deleteFilm_validId_deletesFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1); // Добавляем MPA
        film.setMpa(mpa);
        filmStorage.addFilm(film);

        filmStorage.deleteFilm(film.getId());

        assertFalse(filmStorage.getFilmById(film.getId()).isPresent());
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
        Genre genre = new Genre();
        genre.setId(1);
        film.setGenres(List.of(genre));
        filmStorage.addFilm(film);

        Optional<Film> result = filmStorage.getFilmById(film.getId());

        assertTrue(result.isPresent());
        assertEquals("Test Film", result.get().getName());
        assertEquals(1, result.get().getMpa().getId());
        assertEquals(1, result.get().getGenres().get(0).getId());
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
        Mpa mpa1 = new Mpa();
        mpa1.setId(1); // Добавляем MPA
        film1.setMpa(mpa1);
        filmStorage.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2); // Добавляем MPA
        film2.setMpa(mpa2);
        filmStorage.addFilm(film2);

        List<Film> films = filmStorage.getAllFilms();

        assertEquals(2, films.size());
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("Film 1")));
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("Film 2")));
    }
}