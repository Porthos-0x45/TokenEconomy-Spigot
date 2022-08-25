package me.pixel.tokens;

import org.bukkit.plugin.Plugin;

public class ThisPlugin {

    private static Plugin p;

    public static void constructor(Plugin p) {
        ThisPlugin.p = p;
    }

    public static Plugin get() {
        return p;
    }
}
