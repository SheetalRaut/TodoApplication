package com.example.demo_application.dao;

import com.example.demo_application.model.Todo;
import com.example.demo_application.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoDaoImpl implements TodoDao {

	private static final String INSERT_TODOS_SQL = "INSERT INTO PUBLIC.TODOS"
			+ "  (TITLE, USERNAME, DESCRIPTION, TARGET_DATE, IS_DONE, LAST_MODIFIED_DATE) VALUES " + " (?, ?, ?, ?, ?,?);";

	private static final String SELECT_TODO_BY_ID = "select ID , TITLE, USERNAME, DESCRIPTION, TARGET_DATE, IS_DONE,LAST_MODIFIED_DATE  from PUBLIC.TODOS where ID =?";
	private static final String SELECT_ALL_TODOS = "select * from PUBLIC.TODOS";
	private static final String DELETE_TODO_BY_ID = "delete from PUBLIC.TODOS where id = ?;";
	private static final String UPDATE_TODO = "update PUBLIC.TODOS set TITLE = ?, USERNAME= ?, DESCRIPTION =?, TARGET_DATE =?, IS_DONE = ? , LAST_MODIFIED_DATE = ? where ID = ?;";

	public TodoDaoImpl() {
	}

	@Override
	public void insertTodo(Todo todo) throws SQLException {
		System.out.println(INSERT_TODOS_SQL);
		// try-with-resource statement will auto close the connection.
		try (Connection connection = JDBCUtils.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TODOS_SQL)) {
			preparedStatement.setString(1, todo.getTitle());
			preparedStatement.setString(2, todo.getUsername());
			preparedStatement.setString(3, todo.getDescription());
			preparedStatement.setDate(4, JDBCUtils.getSQLDate(todo.getTargetDate()));
			preparedStatement.setBoolean(5, todo.getStatus());
			preparedStatement.setDate(6, JDBCUtils.getSQLDate(LocalDate.now()));
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
		} catch (SQLException exception) {
			JDBCUtils.printSQLException(exception);
		}
	}

	@Override
	public Todo selectTodo(long todoId) {
		Todo todo = null;
		try (Connection connection = JDBCUtils.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TODO_BY_ID);) {
			preparedStatement.setLong(1, todoId);
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				long id = rs.getLong("ID");
				String title = rs.getString("TITLE");
				String username = rs.getString("USERNAME");
				String description = rs.getString("DESCRIPTION");
				LocalDate targetDate = rs.getDate("TARGET_DATE").toLocalDate();
				LocalDate lastModifiedDate = rs.getDate("LAST_MODIFIED_DATE").toLocalDate();
				boolean isDone = rs.getBoolean("IS_DONE");
				todo = new Todo(id, title, username, description, targetDate, isDone, lastModifiedDate);
			}
		} catch (SQLException exception) {
			JDBCUtils.printSQLException(exception);
		}
		return todo;
	}

	@Override
	public List<Todo> selectAllTodos() {

		List<Todo> todos = new ArrayList<>();
		try (Connection connection = JDBCUtils.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TODOS);) {
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				long id = rs.getLong("ID");
				String title = rs.getString("TITLE");
				String username = rs.getString("USERNAME");
				String description = rs.getString("DESCRIPTION");
				LocalDate targetDate = rs.getDate("TARGET_DATE").toLocalDate();
				LocalDate lastModifiedDate = rs.getDate("LAST_MODIFIED_DATE").toLocalDate();
				boolean isDone = rs.getBoolean("IS_DONE");
				todos.add(new Todo(id, title, username, description, targetDate, isDone, lastModifiedDate));
			}
		} catch (SQLException exception) {
			JDBCUtils.printSQLException(exception);
		}
		return todos;
	}

	@Override
	public boolean deleteTodo(int id) throws SQLException {
		boolean rowDeleted;
		try (Connection connection = JDBCUtils.getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_TODO_BY_ID);) {
			statement.setInt(1, id);
			rowDeleted = statement.executeUpdate() > 0;
		}
		return rowDeleted;
	}

	@Override
	public boolean updateTodo(Todo todo) throws SQLException {
		boolean rowUpdated;
		try (Connection connection = JDBCUtils.getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_TODO);) {
			statement.setString(1, todo.getTitle());
			statement.setString(2, todo.getUsername());
			statement.setString(3, todo.getDescription());
			statement.setDate(4, JDBCUtils.getSQLDate(todo.getTargetDate()));
			statement.setBoolean(5, todo.getStatus());
			statement.setDate(6,JDBCUtils.getSQLDate(LocalDate.now()));
			statement.setLong(7, todo.getId());
			rowUpdated = statement.executeUpdate() > 0;
		}
		return rowUpdated;
	}
}
