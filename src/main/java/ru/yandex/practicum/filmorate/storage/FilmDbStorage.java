package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (title, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        updateFilmGenres(film);
        return getFilmById(film.getId()).orElse(film);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
        updateFilmGenres(film);
        return getFilmById(film.getId()).orElse(film);
    }

    @Override
    public void deleteFilm(Integer id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        String sql = "SELECT f.film_id, f.title, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                "(SELECT COUNT(*) FROM likes WHERE film_id = f.film_id) AS likes_count " +
                "FROM films AS f " +
                "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.get(0);
        film.setGenres(getFilmGenres(film.getId()));
        return Optional.of(film);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.film_id, f.title, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                "(SELECT COUNT(*) FROM likes WHERE film_id = f.film_id) AS likes_count " +
                "FROM films AS f " +
                "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
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

    private void updateFilmGenres(Film film) {
        // Удаляем все существующие жанры для фильма
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        // Добавляем только уникальные жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            Set<Integer> uniqueGenreIds = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                if (uniqueGenreIds.add(genre.getId())) { // Добавляем только уникальные ID жанров
                    jdbcTemplate.update(insertSql, film.getId(), genre.getId());
                }
            }
        }
    }

    private List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genre g JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }
}