package me.pixel.tokens.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter
{
    public List<String> args1 = new ArrayList<>();
    public List<String> args2 = new ArrayList<>();

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Player player = (Player) sender;

        if (this.args1.isEmpty()) {
            if (sender == null)
            {
                return null;
            }

            if (player.hasPermission("token.owner"))
            {
                this.args1.add("help");
                this.args1.add("pay");
                this.args1.add("set");
                this.args1.add("add");
                this.args1.add("balance");
                this.args1.add("remove");
                this.args1.add("deleteUser");
                this.args1.add("reset");
                this.args1.add("reload");
                this.args1.add("top");
                this.args1.add("topall");
                this.args1.add("disable");
                this.args1.add("perms");
                this.args1.add("backup");
            }
            else if (player.hasPermission("token.admin"))
            {
                this.args1.add("help");
                this.args1.add("pay");
                this.args1.add("set");
                this.args1.add("add");
                this.args1.add("remove");
                this.args1.add("reload");
                this.args1.add("balance");
                this.args1.add("top");
                this.args1.add("topall");
                this.args1.add("perms");

            }
            else if (player.hasPermission("token.use"))
            {
                this.args1.add("help");
                this.args1.add("pay");
                this.args1.add("balance");
                this.args1.add("top");
                this.args1.add("topall");
            }
            else
            {
                return null;
            }
        }
        if (this.args2.isEmpty())
        {
            if (player.hasPermission("token.shop"))
            {
                this.args2.add("specials");
                this.args2.add("normal");
            }
            else if (player.hasPermission("token.owner"))
            {
                this.args2.add("specials");
                this.args2.add("normal");
            }
        }

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : this.args1) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }
            return result;
        }
        if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("sell"))
            {
                for (String a : this.args2) {
                    if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
            else if (args[0].equalsIgnoreCase("remItem"))
            {
                for (String a : this.args2) {
                    if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                        result.add(a);
                }
                return result;
            }

        }

        return null;
    }

}