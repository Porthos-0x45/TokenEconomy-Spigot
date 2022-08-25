package me.pixel.tokens.sqlite;

import me.pixel.tokens.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.logging.Level;

public class SQLite
{
    private final Main plugin = Main.getPlugin(Main.class);
    private final String host = plugin.getConfig().getString("database.mysql.host");
    private final String port = plugin.getConfig().getString("database.mysql.port");
    private final String database = plugin.getConfig().getString("database.mysql.databaseName");
    private final String username = plugin.getConfig().getString("database.mysql.user");
    private final String password = plugin.getConfig().getString("database.mysql.password");
    private final File dataFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator  + plugin.getName() + File.separator + "database", database+".db");

    private Connection connection;

    public boolean isConnected()
    {
        return (connection != null);
    }

    public void connect() throws SQLException, ClassNotFoundException {
        if (!Files.isDirectory(dataFolder.toPath())){
            try {
                Files.createFile(dataFolder.toPath());

            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+database+".db");
            }
        }
        else
        {
            if (!isConnected())
            {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            }
        }
    }

    public void refreshConnection()
    {
        try
        {
            getConnection().close();
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
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
