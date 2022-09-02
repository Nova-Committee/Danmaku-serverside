package committee.nova.danmaku.command;

import com.google.common.collect.ImmutableList;
import committee.nova.danmaku.utils.DanmakuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class CommandDanmaku implements TabExecutor {
    public static final ImmutableList<String> helps = ImmutableList.of("/danmaku reload --- Reload the plugin", "/danmaku enable --- enable the danmaku listener", "/danmaku disable --- disable the danmaku listener");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) return false;
        if (args.length == 0) {
            displayHelps(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                DanmakuManager.closeDanmaku();
                DanmakuManager.sendDanmaku("弹幕配置正在重载中……");
                DanmakuManager.openDanmaku();
                return true;
            }
            case "enable": {
                final boolean success = DanmakuManager.openDanmaku();
                sender.sendMessage(success ? "启用弹幕监听！" : "弹幕监听已在运行。");
                return success;
            }
            case "disable": {
                final boolean success = DanmakuManager.closeDanmaku();
                sender.sendMessage(success ? "关闭弹幕监听！" : "弹幕监听未运行。");
                return success;
            }
            default: {
                displayHelps(sender);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? ImmutableList.of("reload", "enable", "disable") : ImmutableList.of();
    }

    public static void displayHelps(CommandSender sender) {
        helps.forEach(sender::sendMessage);
    }
}
