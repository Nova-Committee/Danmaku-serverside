package committee.nova.danmaku.event.post;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class SendDanmakuEvent extends Event {
    String message;

    public SendDanmakuEvent(String message) {
        super(true);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static class Pre extends SendDanmakuEvent implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private boolean isCancelled;

        public Pre(String message) {
            super(message);
        }

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            isCancelled = cancel;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Post extends SendDanmakuEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public Post(String message) {
            super(message);
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }
}
