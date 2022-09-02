package committee.nova.danmaku.event.post;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdatePopularInfoEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final int popular;

    public UpdatePopularInfoEvent(int popular) {
        super(true);
        this.popular = popular;
    }

    public int getPopular() {
        return popular;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
