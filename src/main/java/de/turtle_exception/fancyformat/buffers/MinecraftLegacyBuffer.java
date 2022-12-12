package de.turtle_exception.fancyformat.buffers;

import de.turtle_exception.fancyformat.Buffer;
import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import de.turtle_exception.fancyformat.nodes.StyleNode;
import de.turtle_exception.fancyformat.nodes.TextNode;
import de.turtle_exception.fancyformat.nodes.UnresolvedNode;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MinecraftLegacyBuffer extends Buffer {
    private char code      = ' ';
    private int  codeIndex = -1;
    private int resetIndex = -1;
    private boolean newline;

    private final char[] chars = raw.toCharArray();
    private int index = -1; // incremented before first iteration

    private boolean done = false;

    public MinecraftLegacyBuffer(@NotNull Node parent, @NotNull String raw) {
        super(parent, raw);
    }

    @Override
    public @NotNull List<Node> parse() {
        if (done)
            throw new IllegalStateException("May not reuse MinecraftLegacyBuffer!");
        this.done = true;

        while (++index < chars.length) {
            if (!this.checkCode()) continue;

            // the format code
            char c = chars[index + 1];

            // save code if not already set
            if (codeIndex == -1) {
                // continue if this is not a valid style
                if (getStyle(c) == null) continue;

                code      = c;
                codeIndex = index;
            }

            // check for format reset
            if (c == 'r' || c == '\n') {
                resetIndex = index;
                newline = (c == '\n');
                break;
            }
        }

        // BUILD NODES

        // return as text if no format has been found
        if (codeIndex == -1)
            return this.asText();


        String contentStr = raw.substring(codeIndex + 2, resetIndex != -1 ? resetIndex : raw.length());

        @SuppressWarnings("ConstantConditions") // getStyle() cannot return null here because it has been checked before
        StyleNode        styleNode = new StyleNode(parent, getStyle(code));
        UnresolvedNode contentNode = new UnresolvedNode(styleNode, contentStr, Format.MINECRAFT_LEGACY);
        contentNode.notifyParent();


        ArrayList<Node> nodes = new ArrayList<>();

        if (codeIndex > 0)
            nodes.add(new TextNode(parent, raw.substring(0, codeIndex)));

        nodes.add(styleNode);

        if (resetIndex > codeIndex + 2)
            nodes.add(new UnresolvedNode(parent, raw.substring(resetIndex + (newline ? 0 : 2)), Format.MINECRAFT_LEGACY));

        return nodes;
    }

    private boolean checkCode() {
        // return if not a format char
        if (chars[index] != parent.getFormatter().getMinecraftFormattingCode())
            return false;

        // return if this is the last char
        if (index == chars.length - 1)
            return false;
        return true;
    }

    private Style getStyle(char code) {
        for (Color value : Color.values())
            if (code == value.getCode())
                return value;

        for (FormatStyle value : FormatStyle.values())
            if (code == value.getCode())
                return this.sanitizeStyle(value);

        return null;
    }

    private Style sanitizeStyle(@NotNull FormatStyle style) {
        if (parent instanceof StyleNode sNode) {
            Style parentStyle = sNode.getStyle();

            if (style.equals(FormatStyle.ITALICS) && parentStyle.equals(FormatStyle.BOLD))
                return FormatStyle.ITALICS_ALT;
            if (style.equals(FormatStyle.ITALICS_ALT) && parentStyle.equals(FormatStyle.UNDERLINE))
                return FormatStyle.ITALICS;
        }

        return style;
    }
}
