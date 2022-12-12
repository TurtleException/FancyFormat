package de.turtle_exception.fancyformat.nodes;

import com.google.gson.JsonObject;
import de.turtle_exception.fancyformat.FancyFormatter;
import de.turtle_exception.fancyformat.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClickNode extends ActionNode {
    protected final @NotNull String value;

    public ClickNode(@NotNull FancyFormatter formatter, @Nullable Node parent, @NotNull String action, @NotNull String value) {
        super(formatter, parent, action);
        this.value = value;
    }

    public ClickNode(@NotNull Node parent, @NotNull String action, @NotNull String value) {
        this(parent.getFormatter(), parent, action, value);
    }

    public @NotNull String getValue() {
        return value;
    }

    @Override
    public @NotNull String getType() {
        return "clickEvent";
    }

    @Override
    public @NotNull JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("action", action);
        json.addProperty("value", value);
        return json;
    }
}
