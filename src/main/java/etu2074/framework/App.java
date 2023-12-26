package etu2074.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import etu2074.framework.core.WebCoreApplication;
import etu2074.framework.embedded.database.H2DatabaseManager;
import etu2074.framework.embedded.server.Deployer;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        /*
         * try (Connection connection = H2DatabaseManager.getConnection()) {
         * 
         * String createTableSQL =
         * "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255))"
         * ;
         * try (PreparedStatement statement =
         * connection.prepareStatement(createTableSQL)) {
         * statement.executeUpdate();
         * System.out.println("Table created successfully!");
         * }
         * } catch (SQLException e) {
         * e.printStackTrace();
         * }
         */

        

    }
}
