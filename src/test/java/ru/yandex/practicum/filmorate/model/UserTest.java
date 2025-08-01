package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final Validator validator;

    public UserTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void user_validUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Валидный пользователь не должен иметь нарушений");
    }

    @Test
    void user_emptyEmail_validationFails() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email не должен быть пустым");
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void user_invalidEmail_validationFails() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email должен содержать символ '@'");
        assertEquals("Электронная почта должна содержать символ @", violations.iterator().next().getMessage());
    }

    @Test
    void user_nullEmail_validationFails() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email не должен быть null");
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void user_emptyLogin_validationFails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(null);
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не должен быть пустым");
        assertEquals("Логин не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void user_loginWithSpaces_validationFails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test user");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не должен содержать пробелы");
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void user_nullLogin_validationFails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(null);
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не должен быть null");
        assertEquals("Логин не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void user_emptyName_nameShouldBeLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Пустое имя разрешено, если логин корректен");
        assertEquals("testuser", user.getName(), "Имя должно быть заменено логином, если оно пустое");
    }

    @Test
    void user_nullName_nameShouldBeLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Null имя разрешено, если логин корректен");
        assertEquals("testuser", user.getName(), "Имя должно быть заменено логином, если оно null");
    }

    @Test
    void user_futureBirthday_validationFails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Дата рождения не должна быть в будущем");
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void user_nullBirthday_validationFails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(null);
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Дата рождения не должна быть null");
        assertEquals("Дата рождения не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void user_todayBirthday_validationPasses() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.now());
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Сегодняшняя дата рождения разрешена");
    }
}