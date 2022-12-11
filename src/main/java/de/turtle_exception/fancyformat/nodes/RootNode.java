package de.turtle_exception.fancyformat.nodes;

import de.turtle_exception.fancyformat.FancyFormatter;
import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.Node;
import org.jetbrains.annotations.NotNull;

/** An empty node that functions as a container to hold one or more children. */
public class RootNode extends Node {
    public RootNode(@NotNull FancyFormatter formatter, @NotNull String raw, @NotNull Format format) {
        super(formatter, null);

        this.children.add(new UnresolvedNode(this, raw, format));
    }
}
