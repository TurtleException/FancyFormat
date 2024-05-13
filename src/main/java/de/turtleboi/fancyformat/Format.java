package de.turtleboi.fancyformat;

import de.turtleboi.fancyformat.node.Node;
import de.turtleboi.fancyformat.node.Root;
import org.jetbrains.annotations.NotNull;

public abstract class Format<T> {
    public abstract @NotNull Node[] parse(T text);

    public @NotNull Root parseRoot(T text) {
        return new Root(this.parse(text));
    }

    public abstract T parse(@NotNull Node node);
}
