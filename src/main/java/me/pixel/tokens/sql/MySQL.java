package me.pixel.tokens.sql;

import me.pixel.tokens.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL
{
    private final Main plugin = Main.getPlugin(Main.class);
    private final String host = plugin.getConfig().getString("database.mysql.host");
    private final String port = plugin.getConfig().getString("database.mysql.port");
    private final String database = plugin.getConfig().getString("database.mysql.databaseName");
    private final String username = plugin.getConfig().getString("database.mysql.user");
    private final String password = plugin.getConfig().getString("database.mysql.password");

    private Connection connection;

    public boolean isConnected()
    {
        return (connection == null ? false : true);
    }

    public void connect() throws ClassNotFoundException, SQLException
    {
        if (!isConnected())
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            host + ":" + port + "/" + database + "?useSSL=false",
                    username, password);
        }
    }

    public void refreshConnection()
    {
        try
        {
            getConnection().close();
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            host + ":" + port + "/" + database + "?useSSL=false",
                    username, password);
        } catch (SQLException e)
        {
            plugin.log.debug("Could not connect to MySQL server! because: ", e, false);
        } catch (ClassNotFoundException e)
        {
            plugin.log.debug("JDBC  Driver not found! - ", e, false);
        }
    }

    public void disconnect()
    {
        try
        {
            if (isConnected())
                getConnection().close();
        } catch (SQLException e)
        {
            plugin.log.debug(null, e, false);
        }

    }
    public Connection getConnection()
    {
        return connection;
    }
}
