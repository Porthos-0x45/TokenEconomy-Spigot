package me.pixel.tokens.events;

import me.pixel.tokens.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener
{
    private Main plugin;
    public JoinEvent(Main plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        plugin.sqlGetter.createPlayer(player);
        plugin.liteGetter.createPlayer(player);
    }
}
