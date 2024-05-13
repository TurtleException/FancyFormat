package de.turtleboi.fancyformat.node;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class HoverText extends Node {
    private final Node[] content;

    public HoverText(@NotNull Collection<Node> content, @NotNull Node... children) {
        super(children);
        this.content = content.toArray(Node[]::new);
    }

    public @NotNull Node[] getContent() {
        return this.content;
    }
}
