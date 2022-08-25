package me.pixel.tokens.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Util
{
    public static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static void spamChat(int howMany, Player player, String message)
    {
        for (int i = 0; i < howMany; i++)
        {
            player.sendMessage(chat(message));
        }
    }
}