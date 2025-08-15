package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    public void addFriend(Integer userId, Integer friendId) {
        getUserByIdOrThrow(userId);
        getUserByIdOrThrow(friendId);
        friendshipDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        getUserByIdOrThrow(userId);
        getUserByIdOrThrow(friendId);
        friendshipDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        getUserByIdOrThrow(userId);
        return friendshipDbStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        getUserByIdOrThrow(userId);
        getUserByIdOrThrow(otherId);
        return friendshipDbStorage.getCommonFriends(userId, otherId);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        getUserByIdOrThrow(user.getId());
        return userStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        getUserByIdOrThrow(id);
        userStorage.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        return getUserByIdOrThrow(id);
    }

    private User getUserByIdOrThrow(Integer userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}