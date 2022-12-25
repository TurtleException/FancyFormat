package de.turtle_exception.fancyformat;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public abstract class MessageBuilder<T> {
    protected final @NotNull Node node;

    protected MessageBuilder(@NotNull Node node) {
        this.node = node;
    }

    public abstract @NotNull T build();

    protected @NotNull Gson getGson() {
        return node.getFormatter().getGson();
    }
}
