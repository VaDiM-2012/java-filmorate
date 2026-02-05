package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class})
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Autowired
    GenreDbStorageTest(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Test
    void getGenreById_validId_returnsGenre() {
        Optional<Genre> result = genreStorage.getGenreById(1);

        assertTrue(result.isPresent());
        assertEquals("Комедия", result.get().getName());
    }

    @Test
    void getGenreById_invalidId_returnsEmpty() {
        Optional<Genre> result = genreStorage.getGenreById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllGenres_returnsAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();

        assertEquals(6, genres.size());
        assertTrue(genres.stream().anyMatch(g -> g.getName().equals("Комедия")));
        assertTrue(genres.stream().anyMatch(g -> g.getName().equals("Драма")));
    }
}