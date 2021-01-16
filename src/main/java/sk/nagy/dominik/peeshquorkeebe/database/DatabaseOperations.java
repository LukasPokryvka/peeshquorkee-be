package sk.nagy.dominik.peeshquorkeebe.database;

import sk.nagy.dominik.peeshquorkeebe.restapi.chat.ChatHistory;
import sk.nagy.dominik.peeshquorkeebe.restapi.chat.ChatMessageReceived;
import sk.nagy.dominik.peeshquorkeebe.restapi.login.UserLoginRequest;
import sk.nagy.dominik.peeshquorkeebe.restapi.login.UserLoginResponse;
import sk.nagy.dominik.peeshquorkeebe.restapi.register.UserRegisterRequest;
import sk.nagy.dominik.peeshquorkeebe.restapi.register.UserRegisterResponse;

import java.sql.*;

public class DatabaseOperations extends DatabaseConnection implements IDatabaseOperations{

    private final Connection connection;

    public DatabaseOperations() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        connection = databaseConnection.getConnection();
    }

    // POST(/userLogin)
    // in: email, password
    // out: * except pass
    @Override
    public UserLoginResponse userLogin(UserLoginRequest userLoginRequest) {
        String select = "SELECT * FROM users WHERE email = '" +userLoginRequest.getEmail()+ "'AND password = '" +userLoginRequest.getPassword()+ "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            if (resultSet.next()) {
                connection.close();
                return new UserLoginResponse("Registered", resultSet.getString("ID"),
                        resultSet.getString("nickname"), resultSet.getString("email"), resultSet.getString("avatar"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new UserLoginResponse("Not Registered");
    }

    @Override
    public ChatHistory lastTenMessages(Timestamp timestamp) {
        return null;
    }

    @Override
    public ChatHistory[] fullHistory() {
        String select = "SELECT chat.timestamp, chat.nickname, chat.email, chat.message, avatar\n" +
                "FROM chat INNER JOIN users u on chat.email = u.email\n" +
                "ORDER BY timestamp";

        try {
            PreparedStatement statement = connection.prepareStatement(select,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery();
            resultSet.last();
            ChatHistory[] chatHistories = new ChatHistory[resultSet.getRow()];
            resultSet.beforeFirst();
            int i = 0;
            while (resultSet.next()) {
                chatHistories[i] = new ChatHistory(
                        resultSet.getTimestamp("timestamp"),
                        resultSet.getString("nickname"),
                        resultSet.getString("message"),
                        resultSet.getString("email"));

                i += 1;
            }

            return chatHistories;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public ChatMessageReceived chatMessageReceived(String nickname, String message, String email) {
        return null;
    }

    // POST(/userLogin)
    // in: email, password
    // out: * except pass
    @Override
    public UserRegisterResponse userRegisterResponse(UserRegisterRequest userRegisterRequest) {
        String post = "INSERT INTO users (nickname, email, password, avatar) " +
                "VALUES ('" +userRegisterRequest.getNickname()+ "', '" +userRegisterRequest.getEmail()+ "', '"
                +userRegisterRequest.getPassword()+ "', '" +userRegisterRequest.getAvatar()+ "')";

        try {
            Statement statement = connection.createStatement();
            int resultSet = statement.executeUpdate(post);
            connection.close();
            return new UserRegisterResponse("Success.");
        } catch (SQLException throwables) {
            if (throwables.getErrorCode() == 0) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return new UserRegisterResponse("User already exists.");
            }
            else {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return new UserRegisterResponse("Something went wrong.");
            }
        }
    }
}
