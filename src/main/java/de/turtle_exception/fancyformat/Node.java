package de.turtle_exception.fancyformat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Node {
    protected final @NotNull FancyFormatter formatter;
    protected final @NotNull ArrayList<Node> children = new ArrayList<>();
    protected final @Nullable Node parent;

    protected Node(@NotNull FancyFormatter formatter, @Nullable Node parent) {
        this.formatter = formatter;
        this.parent = parent;
    }

    /* - - - */

    public <T> @NotNull T parse(@NotNull Format<T> format) {
        return format.getMutatorNodeToObject().apply(this);
    }

    public <T> @NotNull String toString(@NotNull Format<T> format) {
        return format.getMutatorObjectToString().apply(this.parse(format));
    }

    @Override
    public String toString() {
        return this.toString(Format.TURTLE);
    }

    public @NotNull FancyFormatter getFormatter() {
        return formatter;
    }

    public @NotNull ArrayList<Node> getChildren() {
        // not an immutable copy because this shouldn't be accessible from outside the application
        return children;
    }

    public @Nullable Node getParent() {
        return parent;
    }

    public @NotNull Node getRoot() {
        if (this.parent != null)
            return parent.getRoot();
        return this;
    }

    public int resolve() {
        int i = 0;
        for (Node child : List.copyOf(this.children))
            i += child.resolve();
        return i;
    }
}
