package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class})
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Autowired
    MpaDbStorageTest(MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Test
    void getMpaById_validId_returnsMpa() {
        Optional<Mpa> result = mpaStorage.getMpaById(1);

        assertTrue(result.isPresent());
        assertEquals("G", result.get().getName());
    }

    @Test
    void getMpaById_invalidId_returnsEmpty() {
        Optional<Mpa> result = mpaStorage.getMpaById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllMpa_returnsAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAllMpa();

        assertEquals(5, mpaList.size());
        assertTrue(mpaList.stream().anyMatch(m -> m.getName().equals("G")));
        assertTrue(mpaList.stream().anyMatch(m -> m.getName().equals("PG")));
    }
}