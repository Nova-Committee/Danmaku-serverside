package committee.nova.danmaku.utils;

import committee.nova.danmaku.Danmaku;
import committee.nova.danmaku.event.post.SendDanmakuEvent;
import committee.nova.danmaku.site.bilibili.BilibiliSite;
import committee.nova.danmaku.websocket.WebSocketClient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import static committee.nova.danmaku.config.ConfigManger.getBilibiliConfig;

public class DanmakuManager {
    public static boolean openDanmaku() {
        if (Danmaku.WEBSOCKET_CLIENT != null) return false;
        BilibiliSite site = new BilibiliSite(getBilibiliConfig());
        if (site.getConfig().getRoom().isEnable()) {
            Danmaku.WEBSOCKET_CLIENT = new WebSocketClient(site);
            try {
                Danmaku.WEBSOCKET_CLIENT.open();
            } catch (Exception e) {
                Danmaku.WEBSOCKET_CLIENT = null;
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean closeDanmaku() {
        if (Danmaku.WEBSOCKET_CLIENT == null) {
            return false;
        }
        try {
            Danmaku.WEBSOCKET_CLIENT.close();
        } catch (Exception ignore) {
        } finally {
            Danmaku.WEBSOCKET_CLIENT = null;
        }
        return true;
    }

    public static boolean sendDanmaku(String msg) {
        final SendDanmakuEvent.Pre p = new SendDanmakuEvent.Pre(msg);
        final PluginManager m = Bukkit.getPluginManager();
        Bukkit.getScheduler().runTaskAsynchronously(Danmaku.instance, () -> m.callEvent(p));
        if (p.isCancelled()) return false;
        final String newMsg = p.getMessage();
        Bukkit.getServer().broadcastMessage(newMsg);
        Bukkit.getScheduler().runTaskAsynchronously(Danmaku.instance, () -> m.callEvent(new SendDanmakuEvent.Post(newMsg)));
        return true;
    }
}
