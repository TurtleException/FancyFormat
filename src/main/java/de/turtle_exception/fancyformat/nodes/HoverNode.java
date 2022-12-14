package de.turtle_exception.fancyformat.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.turtle_exception.fancyformat.FancyFormatter;
import de.turtle_exception.fancyformat.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoverNode extends ActionNode {
    protected final @NotNull JsonElement contents;

    public HoverNode(@NotNull FancyFormatter formatter, @Nullable Node parent, @NotNull String action, @NotNull JsonElement contents) {
        super(formatter, parent, action);
        this.contents = contents;
    }

    public HoverNode(@NotNull Node parent, @NotNull String action, @NotNull JsonElement contents) {
        this(parent.getFormatter(), parent, action, contents);
    }

    public @NotNull JsonElement getContents() {
        return contents;
    }

    @Override
    public @NotNull String getType() {
        return "hoverEvent";
    }

    @Override
    public @NotNull JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("action", action);
        json.add("contents", contents);
        return json;
    }
}
