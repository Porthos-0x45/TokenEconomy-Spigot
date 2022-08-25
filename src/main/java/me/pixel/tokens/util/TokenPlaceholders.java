package me.pixel.tokens.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.pixel.tokens.Main;
import org.bukkit.OfflinePlayer;

public class TokenPlaceholders extends PlaceholderExpansion
{
    private Main plugin;

    public TokenPlaceholders(Main plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor()
    {
        return "Pixel";
    }

    @Override
    public String getIdentifier()
    {
        return "lgcoin";
    }

    @Override
    public String getVersion()
    {
        return "1.0.0";
    }

    @Override
    public boolean persist()
    {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params)
    {
        if(params.equalsIgnoreCase("balance"))
        {
            return Integer.toString(plugin.sqlGetter.getTokens(player.getUniqueId()));
        }
        return null;
    }
}
