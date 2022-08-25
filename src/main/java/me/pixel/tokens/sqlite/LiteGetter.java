package me.pixel.tokens.sqlite;

import me.pixel.tokens.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LiteGetter
{
    private Main plugin;

    public LiteGetter(Main plugin)
    {
        this.plugin = plugin;
    }

    public void createTokenTable()
    {
        PreparedStatement ps = null;
        try
        {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS tokentbl "
                    + "(NAME VARCHAR(100),UUID VARCHAR(100),TOKENS INT(100),PRIMARY KEY (NAME))");
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }
        }
    }
    public void trueReset()
    {
        PreparedStatement ps = null;
        try
        {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("DROP TABLE IF EXISTS shop,shopstate,tokentbl");
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
    }

    public void createBackup()
    {
        for (int i = 0; i < plugin.sqlGetter.getPlayers().toArray().length; i++)
        {
            createPlayerBackup(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString()),
                    plugin.sqlGetter.getName(UUID.fromString(plugin.sqlGetter.getPlayers().toArray()[i].toString())));
        }

        for (UUID player : plugin.sqlGetter.getPlayers())
        {
            setTokens(player, plugin.sqlGetter.getTokens(player));
        }
    }

    public String getName(UUID uuid)
    {
        PreparedStatement ps = null;

        try
        {
            plugin.sqlite.connect();

            ps = plugin.sqlite.getConnection().prepareStatement("SELECT NAME FROM tokentbl WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String name = null;
            if (rs.next())
            {
                name = rs.getString("NAME");
                return name;
            }
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        }
        finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
        return null;
    }
    private void createPlayerBackup(UUID uuid, String name)
    {
        PreparedStatement ps = null;

        try
        {
            if(!playerExists(uuid))
            {
                plugin.sqlite.connect();

                ps = plugin.sqlite.getConnection().prepareStatement("INSERT IGNORE INTO tokentbl " +
                        "(NAME,UUID) VALUES (?, ?)");
                ps.setString(1, name);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();

                return;
            }
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
    }

    public void createPlayer(Player player)
    {
        PreparedStatement ps = null;

        try
        {
            UUID uuid = player.getUniqueId();
            if(!playerExists(uuid))
            {
                plugin.sqlite.connect();

                ps = plugin.sqlite.getConnection().prepareStatement("INSERT IGNORE INTO tokentbl " +
                        "(NAME,UUID) VALUES (?, ?)");
                ps.setString(1, player.getName());
                ps.setString(2, uuid.toString());
                ps.executeUpdate();

                return;
            }
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
    }
    public List<UUID> getPlayers()
    {
        PreparedStatement ps = null;
        try {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("SELECT * FROM tokentbl");

            assert ps != null;
            ResultSet rs = ps.executeQuery();
            List<UUID> pps = new ArrayList<>();
            while (rs.next()) {
                pps.add(UUID.fromString(rs.getString("UUID")));
            }
            rs.close();
            ps.close();
            return pps;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
        return null;
    }
    public void deletePlayer(UUID uuid)
    {
        PreparedStatement ps = null;
        try
        {
            if(playerExists(uuid))
            {
                plugin.sqlite.connect();
                ps = plugin.sqlite.getConnection().prepareStatement("DELETE FROM tokentbl WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
                return;
            }
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
    }
    public boolean playerExists(UUID uuid)
    {
        PreparedStatement ps = null;
        ResultSet results = null;
        try
        {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("SELECT * FROM tokentbl WHERE UUID=?");
            ps.setString(1, uuid.toString());

            results = ps.executeQuery();
            if (results.next())
                return true;

            return false;
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null && results != null)
            {
                try {
                    results.close();
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
        return false;
    }
    public void addTokens(UUID uuid, int tokens)
    {
        PreparedStatement ps = null;
        try
        {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("UPDATE tokentbl SET TOKENS=? WHERE UUID=?");
            ps.setInt(1, (getTokens(uuid) + tokens));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
    }
    public void removeTokens(UUID uuid, int tokens)
    {
        PreparedStatement ps = null;

        try
        {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("UPDATE tokentbl SET TOKENS=? WHERE UUID=?");
            ps.setInt(1, (getTokens(uuid) - tokens));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
    }
    public void setTokens(UUID uuid, int tokens)
    {
        PreparedStatement ps = null;
        try
        {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("UPDATE tokentbl SET TOKENS=? WHERE UUID=?");
            ps.setInt(1, tokens);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null)
            {
                try {
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
    }
    public int getTokens(UUID uuid)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            plugin.sqlite.connect();
            ps = plugin.sqlite.getConnection().prepareStatement("SELECT TOKENS FROM tokentbl WHERE UUID=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            int tokens = 0;
            if (rs.next())
            {
                tokens = rs.getInt("TOKENS");
                return tokens;
            }
        } catch (SQLException | ClassNotFoundException e)
        {
            plugin.log.debug(null, e, false);
        } finally
        {
            if (ps != null && rs != null)
            {
                try {
                    rs.close();
                    ps.close();
                } catch (SQLException e)
                {
                    plugin.log.debug(null, e, false);
                }
            }

        }
        return 0;
    }
}