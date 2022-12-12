package de.turtle_exception.fancyformat.nodes;

import com.google.gson.JsonObject;
import de.turtle_exception.fancyformat.FancyFormatter;
import de.turtle_exception.fancyformat.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ActionNode extends Node {
    protected final @NotNull String action;

    protected ActionNode(@NotNull FancyFormatter formatter, @Nullable Node parent, @NotNull String action) {
        super(formatter, parent);
        this.action = action;
    }

    public @NotNull String getAction() {
        return action;
    }

    public abstract @NotNull String getType();

    public abstract @NotNull JsonObject toJson();
}
