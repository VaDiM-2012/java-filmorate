package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void addUser_validUser_assignsIdAndStores() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.addUser(user);

        assertNotNull(addedUser);
        assertEquals(1, addedUser.getId());
        assertEquals("testuser", addedUser.getLogin());
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @Test
    void updateUser_validUser_updatesExistingUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updateduser");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));

        User result = userStorage.updateUser(updatedUser);
        assertEquals("updateduser", result.getLogin());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated User", result.getName());
        assertEquals(LocalDate.of(1991, 1, 1), result.getBirthday());
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @Test
    void updateUser_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userStorage.updateUser(user));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void deleteUser_validId_removesUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        userStorage.deleteUser(1);
        assertTrue(userStorage.getAllUsers().isEmpty());
    }

    @Test
    void deleteUser_invalidId_throwsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userStorage.deleteUser(999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getUserById_validId_returnsUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        Optional<User> result = userStorage.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getLogin());
    }

    @Test
    void getUserById_invalidId_returnsEmptyOptional() {
        Optional<User> result = userStorage.getUserById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void getAllUsers_returnsAllUsers() {
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

        List<User> users = userStorage.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getLogin().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getLogin().equals("user2")));
    }

    @Test
    void getAllUsers_emptyStorage_returnsEmptyList() {
        List<User> users = userStorage.getAllUsers();
        assertTrue(users.isEmpty());
    }
}