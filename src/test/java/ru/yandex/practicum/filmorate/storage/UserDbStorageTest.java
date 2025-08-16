package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Autowired
    UserDbStorageTest(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Test
    void addUser_validUser_addsUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.addUser(user);

        assertNotNull(addedUser.getId());
        assertEquals("test@example.com", addedUser.getEmail());
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @Test
    void updateUser_validUser_updatesUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updateduser");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));

        User result = userStorage.updateUser(updatedUser);

        assertEquals("updated@example.com", result.getEmail());
        assertEquals("updateduser", result.getLogin());
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @Test
    void deleteUser_validId_deletesUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        userStorage.deleteUser(user.getId());

        assertFalse(userStorage.getUserById(user.getId()).isPresent());
    }

    @Test
    void getUserById_validId_returnsUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        Optional<User> result = userStorage.getUserById(user.getId());

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void getUserById_invalidId_returnsEmpty() {
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
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("test1@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("test2@example.com")));
    }
}