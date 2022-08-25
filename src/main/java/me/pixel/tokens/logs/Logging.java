package me.pixel.tokens.logs;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.bukkit.plugin.Plugin;

public class Logging {
    private static Logger log;
    private static final Logger debugLogger = Logger.getLogger("Logging");
    private FileHandler debugFileHandler;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * Gets the logger for your plugin
     *
     * @param plugin The plugin to apply
     */
    public Logging(Plugin plugin)
    {
        log = plugin.getLogger();

        try {
            LocalDateTime now = LocalDateTime.now();

            debugFileHandler = new FileHandler(plugin.getDataFolder().getAbsolutePath() + File.pathSeparator + plugin.getName()/*
                    + File.separator + dtf.format(now)*/ + ".log", true);
            SimpleFormatter formatter = new SimpleFormatter();

            debugLogger.addHandler(debugFileHandler);
            debugFileHandler.setFormatter(formatter);

        } catch (IOException | SecurityException ex) {
            debug(null, ex, false);
        }
    }

    /**
     * Logs a message
     *
     * @param level The level to log
     * @param msg The message to log
     * @param toFile log to own log?
     */
    public void debug(Level level, String msg, boolean toFile)
    {
        if (toFile) {
            if (debugLogger != null) {
                debugLogger.log(level, msg);
            }
        }
        log.log(level, msg);
    }

    /**
     * Logs an Exception
     *
     * @param msg The message to log
     * @param exception the exception
     * @param toFile log to own log?
     */
    public void debug(String msg, Throwable exception, boolean toFile)
    {
        if (toFile) {
            if (debugLogger != null) {
                debugLogger.log(Level.SEVERE, msg, exception);
            }
        }
        log.log(Level.SEVERE, msg, exception);
    }

}
