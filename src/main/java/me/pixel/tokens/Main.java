package me.pixel.tokens;

import me.pixel.tokens.commands.TokenEcon;
import me.pixel.tokens.events.JoinEvent;
import me.pixel.tokens.logs.Logging;
import me.pixel.tokens.sql.MySQL;
import me.pixel.tokens.sql.SQLGetter;
import me.pixel.tokens.sqlite.LiteGetter;
import me.pixel.tokens.sqlite.SQLite;
import me.pixel.tokens.util.ConfigManager;
import me.pixel.tokens.util.TabComplete;
import me.pixel.tokens.util.TokenPlaceholders;
import me.pixel.tokens.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.logging.Level;

public final class Main extends JavaPlugin
{
    public MySQL mySQL;
    public SQLGetter sqlGetter;
    public SQLite sqlite;
    public LiteGetter liteGetter;
    public PluginManager pm;
    public Logging log;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        ThisPlugin.constructor(this);
        this.log = new Logging(this);
        this.mySQL = new MySQL();
        this.sqlite = new SQLite();
        this.sqlGetter = new SQLGetter(this);
        this.liteGetter = new LiteGetter(this);
        this.pm = Bukkit.getServer().getPluginManager();
        ConfigManager.getInstance().setPlugin(this);
        try
        {
            if (!getConfig().getBoolean("database.sqlite.enabled"))
                mySQL.connect();
        } catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }


        if (mySQL.isConnected()) {
            log.debug(Level.INFO, Util.chat("&aConnection with MySQL has been established!"), false);
            ConfigManager.getInstance().getConfig(getConfig().getString("lang-file"));
            sqlGetter.createTokenTable();
            this.pm.registerEvents(new JoinEvent(this), this);
            getCommand("token").setExecutor(new TokenEcon(this));
            getCommand("token").setTabCompleter(new TabComplete());
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new TokenPlaceholders(this).register();
            }
        } else {
            log.debug(Level.SEVERE, Util.chat(ConfigManager.getInstance().getStringRaw(
                    ConfigManager.getInstance().getConfig(ThisPlugin.get()
                            .getConfig().getString("lang-file")), "err-database-notconnected-message")), true);

            try
            {
                sqlite.connect();
            } catch (SQLException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            if (sqlite.isConnected())
            {
                log.debug(Level.INFO, Util.chat("&aConnection with SQLite has been established!"), false);
                ConfigManager.getInstance().getConfig(getConfig().getString("lang-file"));
                liteGetter.createTokenTable();
                this.pm.registerEvents(new JoinEvent(this), this);
                getCommand("token").setExecutor(new TokenEcon(this));
                getCommand("token").setTabCompleter(new TabComplete());
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    new TokenPlaceholders(this).register();
                }
            }
        }
    }

    @Override
    public void onDisable()
    {
        saveResource(getConfig().getString("lang-file"), false);
        saveResource("config.yml", false);

        if (getConfig().getBoolean("database.backup"))
            liteGetter.createBackup();

        log.debug(Level.INFO, Util.chat("&cDisabling token plugin + disconnecting from database"), false);

        if (mySQL.isConnected())
            mySQL.disconnect();
        else
            sqlite.disconnect();
    }
}