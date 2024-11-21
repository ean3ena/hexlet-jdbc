package io.hexlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDAO {

    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public void save(User user) throws SQLException {

        if (user.getId() == null) {
            var sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.executeUpdate();
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
            }
        }
    }

    public Optional<User> find(Long id) throws SQLException {
        var sql = "SELECT * FROM users WHERE id = ?";
        try (var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var username = resultSet.getString("username");
                var phone = resultSet.getString("phone");
                var user = new User(username, phone);
                user.setId(id);
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }
}
