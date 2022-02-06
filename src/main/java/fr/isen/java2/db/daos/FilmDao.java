package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDao {

	public List<Film> listFilms() {
		List<Film> listFilms = new ArrayList<>();
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (Statement statement = connection.createStatement()) {
				try(ResultSet results = statement.executeQuery("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre")) {
					while (results.next()) {
						Film film = new Film(results.getInt("idfilm"), results.getString("title"), results.getDate("release_date").toLocalDate(),
								 new Genre(results.getInt("genre_id"), results.getString("name")), results.getInt("duration"), results.getString("director"),
										results.getString("summary"));
						listFilms.add(film);
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}	
		catch (Exception e) {
			e.printStackTrace();
		}	
		return listFilms;
	}
	public List<Film> listFilmsByGenre(String genreName) {
		List<Film> listFilms = new ArrayList<>();
		try(Connection  connection = DataSourceFactory.getDataSource().getConnection()) {
			try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM film JOIN genre ON "
					+ "film.genre_id = genre.idgenre WHERE genre.name = ?")){
				statement.setString(1, genreName);
				try(ResultSet results = statement.executeQuery()) {
					while(results.next()) {
						Film film = new Film(results.getInt("idfilm"), results.getString("title"), results.getDate("release_date").toLocalDate(),
								 new Genre(results.getInt("genre_id"), results.getString("name")), results.getInt("duration"), results.getString("director"),
										results.getString("summary"));
						listFilms.add(film);
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
		return listFilms;
	}

	public Film addFilm(Film film) {
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			String sqlQuery = "INSERT INTO film(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, film.getTitle());
				statement.setDate(2, java.sql.Date.valueOf(film.getReleaseDate()));
				statement.setInt(3, film.getGenre().getId());
				statement.setInt(4, film.getDuration());
				statement.setString(5, film.getDirector());
				statement.setString(6, film.getSummary());
				statement.executeUpdate();
				try(ResultSet results = statement.getGeneratedKeys()){
					if(results.next()) {
						return new Film(results.getInt(1), film.getTitle(), film.getReleaseDate(), film.getGenre(), film.getDuration(), film.getDirector(), film.getSummary());
					}
				}
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
