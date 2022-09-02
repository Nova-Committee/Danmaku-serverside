package committee.nova.danmaku;

import committee.nova.danmaku.command.CommandDanmaku;
import committee.nova.danmaku.utils.DanmakuManager;
import committee.nova.danmaku.websocket.WebSocketClient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class Danmaku extends JavaPlugin implements Listener {
    public static Danmaku instance;
    public static final Logger LOGGER = Bukkit.getLogger();
    public static ScheduledFuture<?> HEART_BEAT_TASK = null;
    public static WebSocketClient WEBSOCKET_CLIENT = null;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        final PluginCommand danmaku = this.getCommand("danmaku");
        if (danmaku != null) {
            danmaku.setExecutor(new CommandDanmaku());
            danmaku.setTabCompleter(new CommandDanmaku());
        }
        LOGGER.info(ChatColor.GREEN + "Enabled " + this.getName());
        DanmakuManager.openDanmaku();
    }

    @Override
    public void onDisable() {
        DanmakuManager.closeDanmaku();
    }
}
