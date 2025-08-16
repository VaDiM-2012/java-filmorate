package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FriendshipDbStorage.class, UserDbStorage.class})
class FriendshipDbStorageTest {

    private final FriendshipDbStorage friendshipStorage;
    private final UserDbStorage userStorage;

    @Autowired
    FriendshipDbStorageTest(FriendshipDbStorage friendshipStorage, UserDbStorage userStorage) {
        this.friendshipStorage = friendshipStorage;
        this.userStorage = userStorage;
    }

    @Test
    void addFriend_validUsers_addsFriendship() {
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

        friendshipStorage.addFriend(user1.getId(), user2.getId());

        List<User> friends = friendshipStorage.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals("user2", friends.get(0).getLogin());
    }

    @Test
    void removeFriend_validUsers_removesFriendship() {
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

        friendshipStorage.addFriend(user1.getId(), user2.getId());
        friendshipStorage.removeFriend(user1.getId(), user2.getId());

        List<User> friends = friendshipStorage.getFriends(user1.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriends_noFriends_returnsEmptyList() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        List<User> friends = friendshipStorage.getFriends(user.getId());
        assertTrue(friends.isEmpty());
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

        friendshipStorage.addFriend(user1.getId(), user3.getId());
        friendshipStorage.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = friendshipStorage.getCommonFriends(user1.getId(), user2.getId());
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

        List<User> commonFriends = friendshipStorage.getCommonFriends(user1.getId(), user2.getId());
        assertTrue(commonFriends.isEmpty());
    }
}