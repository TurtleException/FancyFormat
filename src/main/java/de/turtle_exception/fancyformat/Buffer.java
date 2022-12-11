package de.turtle_exception.fancyformat;

import com.google.gson.Gson;
import de.turtle_exception.fancyformat.nodes.TextNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Buffer {
    protected @NotNull Node parent;
    protected @NotNull String raw;

    protected Buffer(@NotNull Node parent, @NotNull String raw) {
        this.parent = parent;
        this.raw = raw;
    }

    public abstract @NotNull List<Node> parse();

    protected @NotNull List<Node> asText() {
        return List.of(new TextNode(parent, raw));
    }

    protected @NotNull Gson getGson() {
        return parent.formatter.getGson();
    }
}
