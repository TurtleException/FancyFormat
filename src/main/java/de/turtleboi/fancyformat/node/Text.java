package de.turtleboi.fancyformat.node;

import org.jetbrains.annotations.NotNull;

public class Text extends Node {
    private final String literal;

    public Text(@NotNull String literal) {
        super();
        this.literal = literal;
    }

    public @NotNull String getLiteral() {
        return this.literal;
    }
}
