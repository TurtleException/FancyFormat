package de.turtleboi.fancyformat.node;

import org.jetbrains.annotations.NotNull;

public class Root extends Node {
    public Root(@NotNull Node... children) {
        super(children);
    }
}
