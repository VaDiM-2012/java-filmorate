package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUserByIdOrThrow(userId);
        User friend = getUserByIdOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserByIdOrThrow(userId);
        User friend = getUserByIdOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Integer userId) {
        User user = getUserByIdOrThrow(userId);
        return user.getFriends().stream()
                .map(id -> getUserByIdOrThrow(id))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserByIdOrThrow(userId);
        User other = getUserByIdOrThrow(otherId);
        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(id -> getUserByIdOrThrow(id))
                .collect(Collectors.toList());
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.getAllUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        if (!userStorage.getAllUsers().stream().anyMatch(u -> u.getId().equals(id))) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
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