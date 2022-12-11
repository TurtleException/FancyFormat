package de.turtle_exception.fancyformat.nodes;

import de.turtle_exception.fancyformat.FancyFormatter;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A node that specifies a {@link Style}. */
public class StyleNode extends Node {
    protected final @NotNull Style style;

    public StyleNode(@NotNull FancyFormatter formatter, @Nullable Node parent, @NotNull Style style) {
        super(formatter, parent);
        this.style = style;
    }

    public StyleNode(@NotNull Node parent, @NotNull Style style) {
        this(parent.getFormatter(), parent, style);
    }

    public @NotNull Style getStyle() {
        return style;
    }
}
