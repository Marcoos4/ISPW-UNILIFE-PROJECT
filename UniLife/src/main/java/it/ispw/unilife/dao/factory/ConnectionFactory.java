package it.ispw.unilife.dao.factory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

    private ConnectionFactory() {
    }

    static {
        try (InputStream input = ConnectionFactory.class.getResourceAsStream("/db.properties")) {

            if (input == null) {
                LOGGER.severe("Unable to load properties file");
            }

            Properties properties = new Properties();
            properties.load(input);

            String connectionUrl = properties.getProperty("CONNECTION_URL");
            String user = properties.getProperty("LOGIN_USER");
            String pass = properties.getProperty("LOGIN_PASS");

            connection = DriverManager.getConnection(connectionUrl, user, pass);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(true);

        } catch (IOException | SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static Connection getConnection(){
        return connection;
    }
}