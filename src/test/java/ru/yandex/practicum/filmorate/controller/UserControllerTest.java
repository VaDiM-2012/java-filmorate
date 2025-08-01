package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    @Test
    void addUser_addValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userController.addUser(user);

        assertNotNull(addedUser);
        assertEquals("testuser", addedUser.getLogin());
        assertEquals(1, addedUser.getId());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void updateUser_updateValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user);

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updateduser");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));

        User result = userController.updateUser(updatedUser);
        assertEquals("updateduser", result.getLogin());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated User", result.getName());
        assertEquals(LocalDate.of(1991, 1, 1), result.getBirthday());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void updateUser_failUpdateNonExistentUser() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(user));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getAllUsers_getAllUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userController.addUser(user2);

        assertEquals(2, userController.getAllUsers().size());
        assertTrue(userController.getAllUsers().contains(user1));
        assertTrue(userController.getAllUsers().contains(user2));
    }

    @Test
    void getAllUsers_getEmptyListWhenNoUsers() {
        assertTrue(userController.getAllUsers().isEmpty(), "Список пользователей должен быть пустым при отсутствии добавленных пользователей");
    }

    @Test
    void getUserById_validId() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user);

        User result = userController.getUserById(1);
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
    }

    @Test
    void getUserById_invalidId_throwsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getUserById(999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void addFriend_validUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userController.addUser(user2);

        userController.addFriend(1, 2);

        List<User> friends1 = userController.getFriends(1);
        List<User> friends2 = userController.getFriends(2);
        assertEquals(1, friends1.size());
        assertEquals("user2", friends1.get(0).getLogin());
        assertEquals(1, friends2.size());
        assertEquals("user1", friends2.get(0).getLogin());
    }

    @Test
    void addFriend_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.addFriend(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void removeFriend_validUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userController.addUser(user2);

        userController.addFriend(1, 2);
        userController.removeFriend(1, 2);

        assertTrue(userController.getFriends(1).isEmpty());
        assertTrue(userController.getFriends(2).isEmpty());
    }

    @Test
    void removeFriend_nonExistentFriendship() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userController.addUser(user2);

        assertDoesNotThrow(() -> userController.removeFriend(1, 2));
        assertTrue(userController.getFriends(1).isEmpty());
        assertTrue(userController.getFriends(2).isEmpty());
    }

    @Test
    void removeFriend_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.removeFriend(1, 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void getFriends_noFriends() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user);

        List<User> friends = userController.getFriends(1);
        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_validUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userController.addUser(user2);

        User user3 = new User();
        user3.setEmail("test3@example.com");
        user3.setLogin("user3");
        user3.setName("User 3");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
        userController.addUser(user3);

        userController.addFriend(1, 3);
        userController.addFriend(2, 3);

        List<User> commonFriends = userController.getCommonFriends(1, 2);
        assertEquals(1, commonFriends.size());
        assertEquals("user3", commonFriends.get(0).getLogin());
    }

    @Test
    void getCommonFriends_noCommonFriends() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userController.addUser(user2);

        List<User> commonFriends = userController.getCommonFriends(1, 2);
        assertTrue(commonFriends.isEmpty());
    }
}