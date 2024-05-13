package de.turtleboi.fancyformat.node;

import org.jetbrains.annotations.NotNull;

public class Color extends Node {
    private final int r;
    private final int g;
    private final int b;

    public Color(int r, int g, int b, @NotNull Node... children) {
        super(children);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(@NotNull String hex, @NotNull Node... children) {
        super(children);
        this.r = Integer.valueOf(hex.substring(0, 2), 16);
        this.g = Integer.valueOf(hex.substring(2, 4), 16);
        this.b = Integer.valueOf(hex.substring(4, 6), 16);
    }

    public Color(int hex, @NotNull Node... children) {
        super(children);
        this.r = (hex & 0xFF0000) >> 16;
        this.g = (hex & 0x00FF00) >> 8;
        this.b = (hex & 0x0000FF);
    }

    public int getRed() {
        return this.r;
    }

    public int getGreen() {
        return this.g;
    }

    public int getBlue() {
        return this.b;
    }

    public java.awt.Color toAWT() {
        return new java.awt.Color(r, g, b);
    }
}
