package me.pixel.tokens.commands;

import me.pixel.tokens.Main;
import me.pixel.tokens.ThisPlugin;
import me.pixel.tokens.util.ConfigManager;
import me.pixel.tokens.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

/*
* I know this is very poorly written, but it works lol
* TODO: Update the whole script: SQLite in commands
*
* @author PixelDev
* */

public class TokenEcon implements CommandExecutor
{
    private Main plugin;

    public TokenEcon(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args)
    {
        plugin.mySQL.refreshConnection();

        if (plugin.getConfig().getBoolean("database.backup"))
        {
            plugin.sqlite.refreshConnection();
            plugin.liteGetter.createBackup();
        }

        if (!(sender instanceof Player))
        {
            if (args.length == 0)
            {
                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                        "wrong-command")));
                return true;
            }


            if (args[0].equalsIgnoreCase("help"))
            {
                TextComponent[] commands = {
                        new TextComponent("/token help"),
                        new TextComponent("/token set <Player> <amount>"),
                        new TextComponent("/token balance <Player>"),
                        new TextComponent("/token add <Player> <amount>"),
                        new TextComponent("/token remove <Player> <amount>"),
                        new TextComponent("/token deleteUser <username>"),
                        new TextComponent("/token reset " + "(Resets everything like literally everything)"),
                        new TextComponent("/token reload " + "(Reloads configuration file)"),
                        new TextComponent("/token disable " + "(Disables plugin)"),
                        new TextComponent("/token top"),
                        new TextComponent("/token topall"),
                        new TextComponent("/token perms"),
                        new TextComponent("/token backup"),
                        new TextComponent("/token connection"),
                        new TextComponent("/token connect"),
                        new TextComponent("/token disconnect")
                };

                if (args.length == 1)
                {
                    sender.sendMessage("\u2582"
                            + "\u2583"
                            + "\u2585"
                            + "\u2586"
                            + " Help commands "
                            + "\u2586"
                            + "\u2585"
                            + "\u2583"
                            + "\u2582");
                    for (TextComponent command : commands)
                    {
                        sender.spigot().sendMessage(command);
                    }
                }
            }

            else if (args[0].equalsIgnoreCase("backup"))
            {
                plugin.getLogger().log(Level.INFO, "Making backup of the database");


                if (plugin.sqlite.isConnected() && plugin.mySQL.isConnected())
                    plugin.liteGetter.createBackup();

                else
                {
                    try
                    {
                        plugin.mySQL.connect();
                        plugin.sqlite.connect();
                        plugin.getLogger().log(Level.WARNING, "Had to connect databases. Try again.");
                    } catch (SQLException | ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            else if (args[0].equalsIgnoreCase("connection"))
            {
                sender.sendMessage("MySQL: " + String.valueOf(plugin.mySQL.getConnection()));
                sender.sendMessage("SQLite: " + String.valueOf(plugin.sqlite.getConnection()));

                if (plugin.mySQL.getConnection() == null)
                    sender.sendMessage("Connection: disconnected");
                else
                    sender.sendMessage("Connection: established");

                plugin.mySQL.refreshConnection();
            }
            else if (args[0].equalsIgnoreCase("connect"))
            {
                sender.sendMessage("Connecting...");
                try
                {
                    plugin.mySQL.connect();
                } catch (ClassNotFoundException | SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }
            else if (args[0].equalsIgnoreCase("disconnect"))
            {
                sender.sendMessage("Disconnecting...");
                plugin.mySQL.disconnect();
            }
            else if (args[0].equalsIgnoreCase("add"))
            {
                if (args.length == 3)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if(target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(ofTarget.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "add-message").replace("%target_name%", ofTarget.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                        return true;
                    } else {
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);

                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(target.getUniqueId(), amount);

                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "add-message").replace("%target_name%", target.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }

                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("top"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                    ThisPlugin.get().getConfig().getString("lang-file")),
                            "no-players"));
                    return true;
                }

                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "top5-command-message"
                        )));

                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter > 5)
                    {
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "want-to-see-all"
                                )));
                        break;
                    }
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("topall"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                    ThisPlugin.get().getConfig().getString("lang-file")),
                            "no-players"));
                    return true;
                }
                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "topall-command-message"
                        )));

                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("disable"))
            {
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "disable-plugin"
                        )));
                plugin.pm.disablePlugin(plugin);
            }
            else if (args[0].equalsIgnoreCase("deleteUser"))
            {
                if (args.length != 2)
                {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }

                Player target = Bukkit.getServer().getPlayer(args[1]);
                if(target == null)
                {
                    OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);

                    if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                    {
                        plugin.sqlGetter.deletePlayer(ofTarget.getUniqueId());
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "removed-user-from-database"
                                ).replace("%target_name%", ofTarget.getName())));
                    }
                    else
                    {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "player-not-found").replace("%target_name%", args[1])));
                    }

                } else
                {
                    if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                    {
                        plugin.sqlGetter.deletePlayer(target.getUniqueId());
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "removed-user-from-database"
                                ).replace("%target_name%", target.getName())));
                    }
                    else
                    {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "player-not-found").replace("%target_name%", args[1])));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                if (args.length != 3)
                {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }

                Player target = Bukkit.getServer().getPlayer(args[1]);
                if(target == null)
                {
                    OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                    if (Util.isInteger(args[2]))
                    {
                        int amount = Integer.parseInt(args[2]);
                        plugin.sqlGetter.removeTokens(ofTarget.getUniqueId(), amount);
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "remove-message"
                                ).replace("%target_name%", ofTarget.getName()).replace("%removed_amount%", String.valueOf(amount))));
                    }
                    return true;
                } else
                {
                    if (Util.isInteger(args[2]))
                    {
                        int amount = Integer.parseInt(args[2]);
                        plugin.sqlGetter.removeTokens(target.getUniqueId(), amount);
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "remove-message"
                                ).replace("%target_name%", target.getName()).replace("%removed_amount%", String.valueOf(amount))));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("reset"))
            {
                plugin.sqlGetter.trueReset();
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "reset-plugin-message"
                        )));
            }
            else if (args[0].equalsIgnoreCase("reload"))
            {
                plugin.reloadConfig();
                ConfigManager.getInstance().reloadConfigs();
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "reload-config"
                        )));
            }
            else if (args[0].equalsIgnoreCase("balance"))
            {
                if (args.length == 2)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", ofTarget.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(ofTarget.getUniqueId())))));
                    } else {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", target.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(target.getUniqueId())))));
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("set"))
            {
                if (args.length == 3)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if(target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.setTokens(ofTarget.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "set-message").replace("%target_name%", ofTarget.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                        return true;
                    } else {
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);

                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.setTokens(target.getUniqueId(), amount);

                                if (ThisPlugin.get().getConfig().getBoolean("set-message")) {
                                    target.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                            "set-receiver-message"
                                    ).replace("%amount%", String.valueOf(amount)).replace("%player_name%", sender.getName())));
                                }

                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "set-message").replace("%target_name%", target.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }

                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else {
                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                        "wrong-command")));
                return true;
            }
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
        {
            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                    "wrong-command")));
            return true;
        }
        if (player.hasPermission("token.owner"))
        {
            if (args[0].equalsIgnoreCase("perms")) {
                TextComponent[] commands = {new TextComponent("/token help"),
                        new TextComponent("/token pay <Player>"),
                        new TextComponent("/token set <Player> <amount>"),
                        new TextComponent("/token add <Player> <amount>"),
                        new TextComponent("/token balance <Player>"),
                        new TextComponent("/token remove <Player> <amount>"),
                        new TextComponent("/token deleteUser <username>"),
                        new TextComponent("/token reset " + "(Resets everything like literally everything)"),
                        new TextComponent("/token reload " + "(Reloads configuration file)"),
                        new TextComponent("/token disable (Disables Plugin)"),
                        new TextComponent("/token top"),
                        new TextComponent("/token topall"),
                        new TextComponent("/token perms"),
                        new TextComponent("/token backup")
                };

                for (TextComponent command : commands) {
                    command.setBold(false);
                    command.setColor(ChatColor.YELLOW);
                    if (command.getText().contains("/token help"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token add"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token backup"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token pay"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token set"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token balance"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token remove"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token deleteUser"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token reset"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token reload"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token disable"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token top"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token topall"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token perms"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                }
                for (TextComponent command : commands)
                {
                    sender.spigot().sendMessage(command);
                }

                //sender.sendMessage(Util.chat("&cDisclaimer: Shop has not been completed yet so dont use commands for that it wont work!"));

                return true;
            }

            else if (args[0].equalsIgnoreCase("backup"))
            {
                sender.sendMessage(Util.chat("&6Making backup of the database..."));

                if (plugin.sqlite.isConnected() && plugin.mySQL.isConnected())
                {
                    plugin.liteGetter.createBackup();

                    sender.sendMessage(Util.chat("&a&lBACKUP CREATED"));
                }
                else
                {
                    try
                    {
                        plugin.mySQL.connect();
                        plugin.sqlite.connect();

                        sender.sendMessage(Util.chat("&eHad to connect databases. Try again."));
                    } catch (SQLException | ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            else if (args[0].equalsIgnoreCase("add"))
            {
                if (args.length == 3)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if(target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(ofTarget.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "add-message").replace("%target_name%", ofTarget.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                        return true;
                    } else {
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);

                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(target.getUniqueId(), amount);

                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "add-message").replace("%target_name%", target.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }

                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("help"))
            {
                TextComponent page1 = new TextComponent("next >");
                TextComponent page2 = new TextComponent("< previous");
                TextComponent[] commands = {
                        new TextComponent("/token help"),
                        new TextComponent("/token pay <Player>"),
                        new TextComponent("/token set <Player> <amount>"),
                        new TextComponent("/token add <Player> <amount>"),
                        new TextComponent("/token balance <Player>"),
                        new TextComponent("/token remove <Player> <amount>"),
                        new TextComponent("/token deleteUser <username>"),
                        new TextComponent("/token reset " + "(Resets everything like literally everything)"),
                        new TextComponent("/token reload " + "(Reloads configuration file)"),
                        new TextComponent("/token disable (Disables Plugin)"),
                        new TextComponent("/token top"),
                        new TextComponent("/token topall"),
                        new TextComponent("/token perms"),
                        new TextComponent("/token backup")
                };

                page1.setBold(true);
                page1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/token help 2"));
                page1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Goes to next page.").color(ChatColor.GRAY).italic(true).create()));
                page2.setBold(true);
                page2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/token help 1"));
                page2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Goes to previous page.").color(ChatColor.GRAY).italic(true).create()));

                for (TextComponent command : commands) {
                    command.setBold(false);
                    command.setColor(ChatColor.YELLOW);
                    command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command.getText()));
                    command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to copy command").color(ChatColor.GRAY).italic(true).create()));
                }

                if (args.length == 1)
                {
                    Util.spamChat(20, player, "\n");
                    sender.sendMessage(ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582" + ChatColor.BOLD + "" + ChatColor.of("#ff00d5") + "\u2583"
                            + ChatColor.BOLD + "" + ChatColor.of("#ff00ab") + "\u2585"
                            + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                            + ChatColor.BOLD + "" + ChatColor.of("#ff0000") + " Help commands "
                            + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                            + ChatColor.BOLD + "" + ChatColor.of("#ff00aa") + "\u2585"
                            + ChatColor.BOLD + "" + ChatColor.of("#ff00d4") + "\u2583"
                            + ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582");
                    for (int i = 0; i < commands.length - 6; i++){
                        sender.spigot().sendMessage(commands[i]);
                    }
                    sender.spigot().sendMessage(page1);
                }
                else if (args.length == 2)
                {
                    int page = Integer.parseInt(args[1]);
                    switch (page)
                    {
                        case 1:
                        {
                            Util.spamChat(20, player, "\n");
                            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582" + ChatColor.BOLD + "" + ChatColor.of("#ff00d5") + "\u2583"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00ab") + "\u2585"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff0000") + " Help commands (Page 1) "
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00aa") + "\u2585"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00d4") + "\u2583"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582");
                            for (int i = 0; i < commands.length - 6; i++){
                                sender.spigot().sendMessage(commands[i]);
                            }
                            sender.spigot().sendMessage(page1);
                            sender.sendMessage("");
                            break;
                        }
                        case 2:
                        {
                            Util.spamChat(20, player, "\n");
                            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582" + ChatColor.BOLD + "" + ChatColor.of("#ff00d5") + "\u2583"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00ab") + "\u2585"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff0000") + " Help commands (Page 2) "
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00aa") + "\u2585"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00d4") + "\u2583"
                                    + ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582");
                            sender.spigot().sendMessage(commands[7]);
                            sender.spigot().sendMessage(commands[8]);
                            sender.spigot().sendMessage(commands[9]);
                            sender.spigot().sendMessage(commands[10]);
                            sender.spigot().sendMessage(commands[11]);
                            sender.spigot().sendMessage(commands[12]);
                            sender.spigot().sendMessage(page2);
                            sender.sendMessage("");
                            break;
                        }
                        default:
                        {
                            Util.spamChat(20, player, "\n");
                            sender.sendMessage(Util.chat(
                                    ConfigManager.getInstance().getStringRaw(
                                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "no-page")));
                            sender.sendMessage("");
                            break;
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("disable"))
            {
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "disable-plugin"
                        )));
                plugin.pm.disablePlugin(plugin);
            }
            else if (args[0].equalsIgnoreCase("top"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                    ThisPlugin.get().getConfig().getString("lang-file")),
                            "no-players"));
                    return true;
                }
                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "top5-command-message"
                        )));
                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter > 5)
                    {
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "want-to-see-all"
                                )));

                        break;
                    }
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("topall"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                    ThisPlugin.get().getConfig().getString("lang-file")),
                            "no-players"));
                    return true;
                }
                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "topall-command-message"
                        )));

                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("reload"))
            {
                ThisPlugin.get().reloadConfig();
                ConfigManager.getInstance().reloadConfigs();
                player.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "reload-config"
                        )));
            }
            else if (args[0].equalsIgnoreCase("reset"))
            {
                plugin.sqlGetter.trueReset();
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "reset-plugin-message"
                        )));
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                if (args.length != 3)
                {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }

                Player target = Bukkit.getServer().getPlayer(args[1]);
                if(target == null)
                {
                    OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                    if (Util.isInteger(args[2]))
                    {
                        int amount = Integer.parseInt(args[2]);
                        plugin.sqlGetter.removeTokens(ofTarget.getUniqueId(), amount);
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "remove-message"
                                ).replace("%target_name%", ofTarget.getName()).replace("%removed_amount%", String.valueOf(amount))));
                    }
                    return true;
                } else
                {
                    if (Util.isInteger(args[2]))
                    {
                        int amount = Integer.parseInt(args[2]);
                        plugin.sqlGetter.removeTokens(target.getUniqueId(), amount);
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "remove-message"
                                ).replace("%target_name%", target.getName()).replace("%removed_amount%", String.valueOf(amount))));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("balance"))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "balance-message-self"
                    ).replace("%player_balance%", String.valueOf(plugin.sqlGetter.getTokens(player.getUniqueId())))));
                    return true;
                }
                else if (args.length == 2)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", ofTarget.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(ofTarget.getUniqueId())))));
                    } else {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", target.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(target.getUniqueId())))));
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("pay"))
            {
                if (args.length == 3)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if(target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (args[1].equals(sender.getName()))
                        {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                            "pay-to-yourself-warn"
                                    )));
                            return true;
                        }

                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (amount > plugin.sqlGetter.getTokens(player.getUniqueId()))
                            {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "not-enough-tokens"
                                        )));
                                return true;
                            }
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(ofTarget.getUniqueId(), amount);
                                plugin.sqlGetter.removeTokens(player.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "pay-message-target"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%target_name%", ofTarget.getName())
                                ));
                            }
                            else
                            {
                                sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                        ThisPlugin.get().getConfig().getString("lang-file")),
                                        "player-not-found"
                                ).replace("%target_name%", args[1]));
                            }
                        }
                        return true;
                    } else {
                        if (args[1].equals(sender.getName()))
                        {
                            sender.sendMessage(Util.chat(
                                    ConfigManager.getInstance().getStringRaw(
                                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                            "pay-to-yourself-warn"
                                    )));
                            return true;
                        }

                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (amount > plugin.sqlGetter.getTokens(player.getUniqueId()))
                            {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "not-enough-tokens"
                                        )));
                                return true;
                            }
                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(target.getUniqueId(), amount);
                                plugin.sqlGetter.removeTokens(player.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "pay-message-target"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%target_name%", target.getName())
                                ));
                                target.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "receive-tokens"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%player_name%", player.getName())));
                            }
                            else
                            {
                                sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                                ThisPlugin.get().getConfig().getString("lang-file")),
                                        "player-not-found"
                                ).replace("%target_name%", args[1]));
                            }
                        }
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("set"))
            {
                if (args.length == 3) {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (Util.isInteger(args[2])) {
                            int amount = Integer.parseInt(args[2]);
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.setTokens(ofTarget.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "set-message").replace("%target_name%", ofTarget.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                        return true;
                    } else {
                        if (Util.isInteger(args[2])) {
                            int amount = Integer.parseInt(args[2]);

                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.setTokens(target.getUniqueId(), amount);

                                if (ThisPlugin.get().getConfig().getBoolean("set-message"))
                                {
                                    target.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                            ConfigManager.getInstance().getConfig("lang-file"),
                                            "set-receiver-message"
                                    ).replace("%transfer_amount%", String.valueOf(amount)).replace("%player_name%", player.getName())));
                                }

                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "set-message").replace("%target_name%", target.getName()).replace("%transfer_amount%",
                                        String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("deleteUser"))
            {
                if (args.length != 2) {

                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }

                Player target = Bukkit.getServer().getPlayer(args[1]);
                if (target == null) {
                    OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);

                    if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                    {
                        plugin.sqlGetter.deletePlayer(ofTarget.getUniqueId());
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "removed-user-from-database"
                                ).replace("%target_name%", ofTarget.getName())));
                    }
                    else
                    {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "player-not-found").replace("%target_name%", args[1])));
                    }
                } else {
                    if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                    {
                        plugin.sqlGetter.deletePlayer(target.getUniqueId());
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "removed-user-from-database"
                                ).replace("%target_name%", target.getName())));
                    }
                    else
                    {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "player-not-found").replace("%target_name%", args[1])));
                    }
                }
            }
            else {
                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                        "wrong-command")));
                return true;
            }
        }
        else if (player.hasPermission("token.admin"))
        {
            if (args[0].equalsIgnoreCase("perms")) {
                TextComponent[] commands = {new TextComponent("/token help"),
                        new TextComponent("/token pay <Player>"),
                        new TextComponent("/token set <Player> <amount>"),
                        new TextComponent("/token balance <Player>"),
                        new TextComponent("/token remove <Player> <amount>"),
                        new TextComponent("/token deleteUser <username>"),
                        new TextComponent("/token reset " + "(Resets everything like literally everything)"),
                        new TextComponent("/token reload " + "(Reloads configuration file)"),
                        new TextComponent("/token disable (Disables Plugin)"),
                        new TextComponent("/token top"),
                        new TextComponent("/token topall"),
                        new TextComponent("/token perms"),
                        new TextComponent("/token add")};

                for (TextComponent command : commands) {
                    command.setBold(false);
                    command.setColor(ChatColor.YELLOW);
                    if (command.getText().contains("/token help"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token pay"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token add"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token set"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token balance"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token remove"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token remItem"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner - Do not use this command").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token shop"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner - Do not use this command").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token sell"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner - Do not use this command").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token cmd"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner - Do not use this command").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token deleteUser"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token reset"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token reload"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token disable"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token top"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token topall"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.use + token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                    else if (command.getText().contains("/token perms"))
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("token.admin + token.owner").color(ChatColor.GRAY).italic(true).create()));
                }
                for (TextComponent command : commands)
                {
                    sender.spigot().sendMessage(command);
                }

                sender.sendMessage(Util.chat("&cDisclaimer: Shop has not been completed yet so dont use commands for that it wont work!"));

                return true;
            }
            else if (args[0].equalsIgnoreCase("add"))
            {
                if (args.length == 3)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if(target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(ofTarget.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "add-message").replace("%target_name%", ofTarget.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                        return true;
                    } else {
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);

                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(target.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "add-message").replace("%target_name%", target.getName())
                                        .replace("%transfer_amount%", String.valueOf(amount))));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }

                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("help"))
            {
                TextComponent[] commands = {
                        new TextComponent("/token help"),
                        new TextComponent("/token pay <Player> <amount>"),
                        new TextComponent("/token balance <Player>"),
                        new TextComponent("/token set <Player> <amount>"),
                        new TextComponent("/token remove <Player> <amount>"),
                        new TextComponent("/token reload " + "(Reloads configuration file)"),
                        new TextComponent("/token shop"),
                        new TextComponent("/token top"),
                        new TextComponent("/token topall"),
                        new TextComponent("/token perms"),
                        new TextComponent("/token add")
                };

                for (TextComponent command : commands) {
                    command.setBold(false);
                    command.setColor(ChatColor.YELLOW);
                    command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command.getText()));
                    command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to copy command").color(ChatColor.GRAY).italic(true).create()));
                }

                Util.spamChat(20, player, "\n");
                sender.sendMessage(ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582" + ChatColor.BOLD + "" + ChatColor.of("#ff00d5") + "\u2583"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00ab") + "\u2585"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff0000") + " Help commands "
                        + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00aa") + "\u2585"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00d4") + "\u2583"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582");
                for (TextComponent command : commands) {
                    sender.spigot().sendMessage(command);
                }
                sender.sendMessage("");
            }
            else if (args[0].equalsIgnoreCase("set"))
            {
                if (args.length == 3) {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (Util.isInteger(args[2])) {
                            int amount = Integer.parseInt(args[2]);
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.setTokens(ofTarget.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "set-message").replace("%target_name%", ofTarget.getName())));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                        return true;
                    } else {
                        if (Util.isInteger(args[2])) {
                            int amount = Integer.parseInt(args[2]);

                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.setTokens(target.getUniqueId(), amount);

                                if (ThisPlugin.get().getConfig().getBoolean("set-message"))
                                {
                                    target.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                            ConfigManager.getInstance().getConfig("lang-file"),
                                            "set-receiver-message"
                                    ).replace("%amount%", String.valueOf(amount)).replace("%player_name%", player.getName())));
                                }

                                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig("lang.yml"),
                                        "set-message").replace("%target_name%", target.getName())));
                            }
                            else {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "player-not-found"
                                        ).replace("%target_name%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "wrong-command")));
                            return true;
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("reload"))
            {
                plugin.reloadConfig();
                ConfigManager.getInstance().reloadConfigs();
                player.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "reload-config"
                        )));
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                if (args.length != 3)
                {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }

                Player target = Bukkit.getServer().getPlayer(args[1]);
                if(target == null)
                {
                    OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                    if (Util.isInteger(args[2]))
                    {
                        int amount = Integer.parseInt(args[2]);
                        plugin.sqlGetter.removeTokens(ofTarget.getUniqueId(), amount);
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "remove-message"
                                ).replace("%target_name%", ofTarget.getName()).replace("%removed_amount%", String.valueOf(amount))));
                    }
                    return true;
                } else
                {
                    if (Util.isInteger(args[2]))
                    {
                        int amount = Integer.parseInt(args[2]);
                        plugin.sqlGetter.removeTokens(target.getUniqueId(), amount);
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "remove-message"
                                ).replace("%target_name%", target.getName()).replace("%removed_amount%", String.valueOf(amount))));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("balance"))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "balance-message-self"
                    ).replace("%player_balance%", String.valueOf(plugin.sqlGetter.getTokens(player.getUniqueId())))));
                    return true;
                }
                else if (args.length == 2)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", ofTarget.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(ofTarget.getUniqueId())))));
                    } else {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", target.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(target.getUniqueId())))));
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("top"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                    ThisPlugin.get().getConfig().getString("lang-file")),
                            "no-players"));
                    return true;
                }
                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "top5-command-message"
                        )));

                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter > 5)
                    {
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(
                                                ThisPlugin.get().getConfig().getString("lang-file")),
                                        "want-to-see-all"
                                )
                        ));
                        break;
                    }
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("topall"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                ThisPlugin.get().getConfig().getString("lang-file")),
                        "no-players"));
                    return true;
                }

                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "topall-command-message"
                        )));

                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("pay"))
            {
                if (args.length == 3) {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (args[1].equals(sender.getName())) {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "pay-to-yourself-warn"
                            )));
                            return true;
                        }

                        if (Util.isInteger(args[2])) {
                            int amount = Integer.parseInt(args[2]);
                            if (amount > plugin.sqlGetter.getTokens(player.getUniqueId())) {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "not-enough-tokens"
                                        )));
                                return true;
                            }
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(ofTarget.getUniqueId(), amount);
                                plugin.sqlGetter.removeTokens(player.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "pay-message-target"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%target_name%", ofTarget.getName())
                                ));
                            }
                            else
                            {
                                sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                                ThisPlugin.get().getConfig().getString("lang-file")),
                                        "player-not-found"
                                ).replace("%target_name%", args[1]));
                            }
                        }
                        return true;
                    } else {
                        if (args[1].equals(sender.getName())) {
                            sender.sendMessage(Util.chat(
                                    ConfigManager.getInstance().getStringRaw(
                                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                            "pay-to-yourself-warn"
                                    )));
                            return true;
                        }

                        if (Util.isInteger(args[2])) {
                            int amount = Integer.parseInt(args[2]);
                            if (amount > plugin.sqlGetter.getTokens(player.getUniqueId())) {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "not-enough-tokens"
                                        )));
                                return true;
                            }
                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(target.getUniqueId(), amount);
                                plugin.sqlGetter.removeTokens(player.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "pay-message-target"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%target_name%", target.getName())
                                ));
                                target.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "receive-tokens"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%player_name%", player.getName())));
                            }
                            else
                            {
                                sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                                ThisPlugin.get().getConfig().getString("lang-file")),
                                        "player-not-found"
                                ).replace("%target_name%", args[1]));
                            }
                        }
                    }
                } else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else
            {
                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                        "wrong-command")));
                return true;
            }
        }
        else if (player.hasPermission("token.use"))
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                TextComponent[] commands = { new TextComponent("/token help"),
                        new TextComponent("/token pay <Player> <amount>"),
                        new TextComponent("/token balance <Player>"),
                        new TextComponent("/token top"),
                        new TextComponent("/token topall") };

                for (TextComponent command : commands) {
                    command.setBold(false);
                    command.setColor(ChatColor.YELLOW);
                    command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command.getText()));
                    command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to copy command").color(ChatColor.GRAY).italic(true).create()));
                }

                Util.spamChat(20, player, "\n");
                sender.sendMessage(ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582" + ChatColor.BOLD + "" + ChatColor.of("#ff00d5") + "\u2583"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00ab") + "\u2585"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff0000") + " Help commands "
                        + ChatColor.BOLD + "" + ChatColor.of("#ff0080") + "\u2586"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00aa") + "\u2585"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00d4") + "\u2583"
                        + ChatColor.BOLD + "" + ChatColor.of("#ff00ff") + "\u2582");
                for (TextComponent command : commands) {
                    sender.spigot().sendMessage(command);
                }
                sender.sendMessage("");
            }
            else if (args[0].equalsIgnoreCase("balance"))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "balance-message-self"
                    ).replace("%player_balance%", String.valueOf(plugin.sqlGetter.getTokens(player.getUniqueId())))));
                    return true;
                }
                else if (args.length == 2)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", ofTarget.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(ofTarget.getUniqueId())))));
                    } else {
                        sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "balance-message-target"
                        ).replace("%target_name%", target.getName()).replace("%target_balance%",
                                String.valueOf(plugin.sqlGetter.getTokens(target.getUniqueId())))));
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("top"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                    ThisPlugin.get().getConfig().getString("lang-file")),
                            "no-players"));
                    return true;
                }
                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "top5-command-message"
                        )));

                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter > 5)
                    {
                        sender.sendMessage(Util.chat(
                                ConfigManager.getInstance().getStringRaw(
                                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                        "want-to-see-all"
                                )));
                        break;
                    }
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("topall"))
            {
                if (plugin.sqlGetter.getPlayers() == null) {
                    sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                    ThisPlugin.get().getConfig().getString("lang-file")),
                            "no-players"));
                    return true;
                }
                HashMap<String, Integer> ogData = new HashMap<String, Integer>();
                int counter = 1;
                sender.sendMessage(Util.chat(
                        ConfigManager.getInstance().getStringRaw(
                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                "topall-command-message"
                        )));

                for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
                {
                    ogData.put(plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())),
                            plugin.sqlGetter.getTokens(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
                }

                Object[] a = ogData.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    if (counter == 1)
                        sender.sendMessage(Util.chat("&e" + counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue()));
                    else
                        sender.sendMessage(counter + ". " + ((Map.Entry<String, Integer>) e).getKey() + " : "
                                + ((Map.Entry<String, Integer>) e).getValue());
                    counter++;
                }

                ogData.clear();
            }
            else if (args[0].equalsIgnoreCase("pay"))
            {
                if (args.length == 3)
                {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if(target == null) {
                        OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(args[1]);
                        if (args[1].equals(sender.getName()))
                        {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "pay-to-yourself-warn"
                            )));
                            return true;
                        }

                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (amount > plugin.sqlGetter.getTokens(player.getUniqueId()))
                            {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "not-enough-tokens"
                                        )));
                                return true;
                            }
                            if (plugin.sqlGetter.playerExists(ofTarget.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(ofTarget.getUniqueId(), amount);
                                plugin.sqlGetter.removeTokens(player.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "pay-message-target"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%target_name%", ofTarget.getName())
                                ));
                            }
                            else
                            {
                                sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                                ThisPlugin.get().getConfig().getString("lang-file")),
                                        "player-not-found"
                                ).replace("%target_name%", args[1]));
                            }
                        }
                        return true;
                    } else {
                        if (args[1].equals(sender.getName()))
                        {
                            sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                                    ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                    "pay-to-yourself-warn"
                            )));
                            return true;
                        }
                        if (Util.isInteger(args[2]))
                        {
                            int amount = Integer.parseInt(args[2]);
                            if (amount > plugin.sqlGetter.getTokens(player.getUniqueId()))
                            {
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "not-enough-tokens"
                                        )));
                                return true;
                            }
                            if (plugin.sqlGetter.playerExists(target.getUniqueId()))
                            {
                                plugin.sqlGetter.addTokens(target.getUniqueId(), amount);
                                plugin.sqlGetter.removeTokens(player.getUniqueId(), amount);
                                sender.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "pay-message-target"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%target_name%", target.getName())
                                ));
                                target.sendMessage(Util.chat(
                                        ConfigManager.getInstance().getStringRaw(
                                                ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                                                "receive-tokens"
                                        ).replace("%transfer_amount%", String.valueOf(amount)).replace("%player_name%", player.getName())));
                            }
                            else
                            {
                                sender.sendMessage(ConfigManager.getInstance().getStringRaw(ConfigManager.getInstance().getConfig(
                                                ThisPlugin.get().getConfig().getString("lang-file")),
                                        "player-not-found"
                                ).replace("%target_name%", args[1]));
                            }
                        }
                    }
                }
                else {
                    sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                            ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                            "wrong-command")));

                    return true;
                }
            }
            else {
                sender.sendMessage(Util.chat(ConfigManager.getInstance().getStringRaw(
                        ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                        "wrong-command")));

                return true;
            }
        }
        else
        {
            sender.sendMessage(Util.chat(
               ConfigManager.getInstance().getStringRaw(
                       ConfigManager.getInstance().getConfig(ThisPlugin.get().getConfig().getString("lang-file")),
                       "no-permission"
               )));
            return true;
        }

        return true;
    }
}