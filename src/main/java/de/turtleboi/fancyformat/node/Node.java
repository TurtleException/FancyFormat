package de.turtleboi.fancyformat.node;

import org.jetbrains.annotations.NotNull;

public abstract class Node {
    private final Node[] children;
    private Node parent;

    public Node(@NotNull Node... children) {
        this.children = children;

        for (Node child : this.children)
            child.parent = this;
    }

    public @NotNull Node[] getChildren() {
        return this.children;
    }
}
