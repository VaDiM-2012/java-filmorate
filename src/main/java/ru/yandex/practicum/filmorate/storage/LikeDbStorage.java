package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addLike(int filmId, int userId) {
        String sql = "MERGE INTO likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
        return films;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("title"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setRate(resultSet.getInt("likes_count"));

        Integer mpaId = resultSet.getObject("mpa_id", Integer.class);
        if (mpaId != null) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
        }

        return film;
    }

    private List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genre g JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }
}