package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Добавление нового пользователя: {}", user.getLogin());
        validateUser(user);
        try {
            user.setId(nextId++);
            users.put(user.getId(), user);
            log.info("Пользователь успешно добавлен: {}", user.getLogin());
            return user;
        } catch (Exception e) {
            log.error("Ошибка валидации при добавлении пользователя: {}", e.getMessage());
            throw new ValidationException(e.getMessage());
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Обновление пользователя с id: {}", user.getId());
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new ValidationException("Пользователь не найден");
        }
        validateUser(user);
        try {
            users.put(user.getId(), user);
            log.info("Пользователь успешно обновлен: {}", user.getLogin());
            return user;
        } catch (Exception e) {
            log.error("Ошибка валидации при обновлении пользователя: {}", e.getMessage());
            throw new ValidationException(e.getMessage());
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}