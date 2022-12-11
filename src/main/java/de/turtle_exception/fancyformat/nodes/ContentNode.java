package de.turtle_exception.fancyformat.nodes;

import org.jetbrains.annotations.NotNull;

/** A node that has text content. */
public interface ContentNode {
    @NotNull String getContent();
}
