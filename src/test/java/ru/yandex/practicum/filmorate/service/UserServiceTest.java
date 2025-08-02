package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    void addFriend_validUsers_addsMutualFriendship() {
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

        userService.addFriend(1, 2);

        User updatedUser1 = userStorage.getUserById(1).get();
        User updatedUser2 = userStorage.getUserById(2).get();
        assertTrue(updatedUser1.getFriends().contains(2));
        assertTrue(updatedUser2.getFriends().contains(1));
    }

    @Test
    void addFriend_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.addFriend(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeFriend_validUsers_removesMutualFriendship() {
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

        userService.addFriend(1, 2);
        userService.removeFriend(1, 2);

        User updatedUser1 = userStorage.getUserById(1).get();
        User updatedUser2 = userStorage.getUserById(2).get();
        assertFalse(updatedUser1.getFriends().contains(2));
        assertFalse(updatedUser2.getFriends().contains(1));
    }

    @Test
    void removeFriend_nonExistentFriendship_noError() {
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

        assertDoesNotThrow(() -> userService.removeFriend(1, 2));
        User updatedUser1 = userStorage.getUserById(1).get();
        User updatedUser2 = userStorage.getUserById(2).get();
        assertFalse(updatedUser1.getFriends().contains(2));
        assertFalse(updatedUser2.getFriends().contains(1));
    }

    @Test
    void removeFriend_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.removeFriend(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getFriends_validUser_returnsFriendList() {
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

        userService.addFriend(1, 2);

        List<User> friends = userService.getFriends(1);
        assertEquals(1, friends.size());
        assertEquals("user2", friends.get(0).getLogin());
    }

    @Test
    void getFriends_noFriends_returnsEmptyList() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        List<User> friends = userService.getFriends(1);
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriends_invalidUserId_throwsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getFriends(999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getCommonFriends_validUsers_returnsCommonFriends() {
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

        User user3 = new User();
        user3.setEmail("test3@example.com");
        user3.setLogin("user3");
        user3.setName("User 3");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
        userStorage.addUser(user3);

        userService.addFriend(1, 3);
        userService.addFriend(2, 3);

        List<User> commonFriends = userService.getCommonFriends(1, 2);
        assertEquals(1, commonFriends.size());
        assertEquals("user3", commonFriends.get(0).getLogin());
    }

    @Test
    void getCommonFriends_noCommonFriends_returnsEmptyList() {
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

        List<User> commonFriends = userService.getCommonFriends(1, 2);
        assertTrue(commonFriends.isEmpty());
    }

    @Test
    void getCommonFriends_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getCommonFriends(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }
}