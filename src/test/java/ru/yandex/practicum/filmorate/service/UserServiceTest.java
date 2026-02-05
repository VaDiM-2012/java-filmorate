package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserService.class, UserDbStorage.class, FriendshipDbStorage.class})
class UserServiceTest {

    private final UserService userService;
    private final UserDbStorage userStorage;
    private final FriendshipDbStorage friendshipStorage;

    @Autowired
    UserServiceTest(UserService userService, UserDbStorage userStorage, FriendshipDbStorage friendshipStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
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

        userService.addFriend(user1.getId(), user2.getId());

        List<User> friends1 = friendshipStorage.getFriends(user1.getId());
        assertEquals(1, friends1.size(), "User should have 1 friend");
        assertEquals("user2", friends1.get(0).getLogin(), "User's friend should be user2");
    }

    @Test
    void addFriend_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.addFriend(user.getId(), 999));
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

        userService.addFriend(user1.getId(), user2.getId());
        userService.removeFriend(user1.getId(), user2.getId());

        List<User> friends1 = friendshipStorage.getFriends(user1.getId());
        List<User> friends2 = friendshipStorage.getFriends(user2.getId());
        assertTrue(friends1.isEmpty(), "User 1 should have no friends after removal");
        assertTrue(friends2.isEmpty(), "User 2 should have no friends after removal");
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

        assertDoesNotThrow(() -> userService.removeFriend(user1.getId(), user2.getId()));
        List<User> friends1 = friendshipStorage.getFriends(user1.getId());
        List<User> friends2 = friendshipStorage.getFriends(user2.getId());
        assertTrue(friends1.isEmpty(), "User 1 should have no friends");
        assertTrue(friends2.isEmpty(), "User 2 should have no friends");
    }

    @Test
    void removeFriend_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.removeFriend(user.getId(), 999));
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

        userService.addFriend(user1.getId(), user2.getId());

        List<User> friends = userService.getFriends(user1.getId());
        assertEquals(1, friends.size(), "User 1 should have 1 friend");
        assertEquals("user2", friends.get(0).getLogin(), "Friend should be user2");
    }

    @Test
    void getFriends_noFriends_returnsEmptyList() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        List<User> friends = userService.getFriends(user.getId());
        assertTrue(friends.isEmpty(), "Friend list should be empty");
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

        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, commonFriends.size(), "There should be 1 common friend");
        assertEquals("user3", commonFriends.get(0).getLogin(), "Common friend should be user3");
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

        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());
        assertTrue(commonFriends.isEmpty(), "Common friends list should be empty");
    }

    @Test
    void getCommonFriends_invalidUserId_throwsNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getCommonFriends(user.getId(), 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }
}