package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public void addLike(Integer filmId, Integer userId) {
        getFilmByIdOrThrow(filmId);
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        likeDbStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        getFilmByIdOrThrow(filmId);
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        likeDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return likeDbStorage.getPopularFilms(count);
    }

    public Film addFilm(Film film) {
        validateMpaAndGenres(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilmByIdOrThrow(film.getId());
        validateMpaAndGenres(film);
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Integer id) {
        getFilmByIdOrThrow(id);
        filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        return getFilmByIdOrThrow(id);
    }

    private Film getFilmByIdOrThrow(Integer filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaDbStorage.getMpaById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id " + film.getMpa().getId() + " не найден"));
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreDbStorage.getGenreById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден"));
            }
        }
    }
}