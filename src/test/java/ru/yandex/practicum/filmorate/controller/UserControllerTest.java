package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
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
        assertEquals(1, userController.getAllUsers().size()); // Проверяем, что количество пользователей не изменилось
    }

    @Test
    void updateUser_failUpdateNonExistentUser() {
        User user = new User();
        user.setId(999); // ID, которого нет в коллекции
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(user));
        assertEquals("Пользователь не найден", exception.getMessage());
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
}