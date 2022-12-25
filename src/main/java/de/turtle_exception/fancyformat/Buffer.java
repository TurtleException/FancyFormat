package de.turtle_exception.fancyformat;

import com.google.gson.Gson;
import de.turtle_exception.fancyformat.nodes.TextNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Buffer<T> {
    protected @NotNull Node parent;
    protected @NotNull T raw;

    private final @NotNull Format<T> format;

    protected Buffer(@NotNull Node parent, @NotNull T raw, @NotNull Format<T> format) {
        this.parent = parent;
        this.raw = raw;

        this.format = format;
    }

    public abstract @NotNull List<Node> parse();

    protected @NotNull List<Node> asText() {
        return List.of(new TextNode(parent, format.getMutatorObjectToString().apply(raw)));
    }

    protected @NotNull Gson getGson() {
        return parent.formatter.getGson();
    }
}
