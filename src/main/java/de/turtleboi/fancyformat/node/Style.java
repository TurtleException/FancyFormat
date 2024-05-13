package de.turtleboi.fancyformat.node;

import org.jetbrains.annotations.NotNull;

public class Style extends Node {
    private final Type type;

    public Style(@NotNull Type type, @NotNull Node... children) {
        super(children);
        this.type = type;
    }

    public @NotNull Type getType() {
        return this.type;
    }

    public boolean isBold() {
        return this.getType() == Type.BOLD;
    }

    public boolean isItalic() {
        return this.getType() == Type.ITALICS;
    }

    public boolean isUnderlined() {
        return this.getType() == Type.UNDERLINE;
    }

    public boolean isStrikethrough() {
        return this.getType() == Type.STRIKETHROUGH;
    }

    public enum Type {
        BOLD,
        ITALICS,
        UNDERLINE,
        STRIKETHROUGH
    }
}
