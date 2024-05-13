package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.Format;
import de.turtleboi.fancyformat.node.*;
import de.turtleboi.fancyformat.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MinecraftLegacyFormat extends Format<String> {
    private static final char COLOR_BLACK        = '0';
    private static final char COLOR_DARK_BLUE    = '1';
    private static final char COLOR_DARK_GREEN   = '2';
    private static final char COLOR_DARK_AQUA    = '3';
    private static final char COLOR_DARK_RED     = '4';
    private static final char COLOR_DARK_PURPLE  = '5';
    private static final char COLOR_GOLD         = '6';
    private static final char COLOR_GRAY         = '7';
    private static final char COLOR_DARK_GRAY    = '8';
    private static final char COLOR_BLUE         = '9';
    private static final char COLOR_GREEN        = 'a';
    private static final char COLOR_AQUA         = 'b';
    private static final char COLOR_RED          = 'c';
    private static final char COLOR_LIGHT_PURPLE = 'd';
    private static final char COLOR_YELLOW       = 'e';
    private static final char COLOR_WHITE        = 'f';

    private static final char STYLE_OBFUSCATE     = 'k';
    private static final char STYLE_BOLD          = 'l';
    private static final char STYLE_STRIKETHROUGH = 'm';
    private static final char STYLE_UNDERLINE     = 'n';
    private static final char STYLE_ITALICS       = 'o';

    private static final char RESET = 'r';

    private final char indicator;

    public MinecraftLegacyFormat(char indicator) {
        this.indicator = indicator;
    }

    public static @NotNull MinecraftLegacyFormat createDefault() {
        return new MinecraftLegacyFormat('§');
    }

    @Override
    public @NotNull Node[] parse(String text) {
        return this.parse0(text);
    }

    @Override
    public String parse(@NotNull Node node) {
        StringBuilder builder = new StringBuilder(this.parse0(node));

        this.removeLeadingResets(builder);
        this.removeRepeatedResets(builder);

        return builder.toString();
    }

    private void removeLeadingResets(@NotNull StringBuilder builder) {
        int length = builder.length();

        // indicates whether the previous character was equal to indicator
        boolean isFormat = false;

        for (int i = 0; i < length; i++) {
            char c = builder.charAt(i);

            if (c == indicator) {
                isFormat = true;
                continue;
            }

            if (!isFormat)
                continue;
            isFormat = false;

            // return if any non-reset formatting is done

            // STYLES
            if (Util.equalsAny(c, STYLE_BOLD, STYLE_ITALICS, STYLE_UNDERLINE, STYLE_STRIKETHROUGH))
                return;

            // HIDDEN
            if (c == STYLE_OBFUSCATE)
                return;

            // COLOR
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
                return;

            if (c == RESET) {
                // delete RESET char
                builder.deleteCharAt(i);
                // delete indicator char
                builder.deleteCharAt(i - 1);

                // adjust to shorter length
                length -= 2;
                // reset cursor
                i -= 2;
            }
        }
    }

    private void removeRepeatedResets(@NotNull StringBuilder builder) {
        int length = builder.length();

        // indicates whether the previous character was equal to indicator
        boolean isFormat = false;

        // indicates whether the cursor is in a formatted section
        boolean inFormat = false;

        for (int i = 0; i < length; i++) {
            char c = builder.charAt(i);

            if (c == indicator) {
                isFormat = true;
                continue;
            }

            if (!isFormat)
                continue;
            isFormat = false;

            // STYLES
            if (Util.equalsAny(c, STYLE_BOLD, STYLE_ITALICS, STYLE_UNDERLINE, STYLE_STRIKETHROUGH)) {
                inFormat = true;
                continue;
            }

            // HIDDEN
            if (c == STYLE_OBFUSCATE) {
                inFormat = true;
                continue;
            }

            // COLOR
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                inFormat = true;
                continue;
            }

            if (c == RESET) {
                // reset is valid
                if (inFormat) {
                    inFormat = false;
                    continue;
                }

                // delete RESET char
                builder.deleteCharAt(i);
                // delete indicator char
                builder.deleteCharAt(i - 1);

                // adjust to shorter length
                length -= 2;
                // reset cursor
                i -= 2;
            }
        }
    }

    private @NotNull Node[] parse0(@NotNull String text) {
        List<Node> nodes = new ArrayList<>();

        // indicates whether the previous character was equal to indicator
        boolean isFormat = false;

        // overwrites the beginning of the string (0)
        int marker = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == indicator) {
                isFormat = true;
                continue;
            }

            if (!isFormat)
                continue;
            isFormat = false;

            if (Util.equalsAny(c, STYLE_BOLD, STYLE_ITALICS, STYLE_UNDERLINE, STYLE_STRIKETHROUGH)) {
                Style.Type type = switch (c) {
                    case STYLE_BOLD          -> Style.Type.BOLD;
                    case STYLE_ITALICS       -> Style.Type.ITALICS;
                    case STYLE_UNDERLINE     -> Style.Type.UNDERLINE;
                    case STYLE_STRIKETHROUGH -> Style.Type.STRIKETHROUGH;
                    default -> throw new Error("Unreachable");
                };

                // leading text
                if (i - marker > 2) {
                    String leadingText = text.substring(marker, i - 1);
                    Text leadingTextNode = new Text(leadingText);
                    nodes.add(leadingTextNode);
                }

                marker = i + 1;

                // find next reset
                int reset = getNextReset(text, marker);

                String substring = text.substring(marker, reset);
                Node[] children = this.parse0(substring);

                Style styleNode = new Style(type, children);

                nodes.add(styleNode);

                // jump marker to next style
                marker = reset;

                // jump to end of this style
                i = marker - 1;
                continue;
            }

            if (c == STYLE_OBFUSCATE) {
                // leading text
                if (i - marker > 2) {
                    String leadingText = text.substring(marker, i - 1);
                    Text leadingTextNode = new Text(leadingText);
                    nodes.add(leadingTextNode);
                }

                marker = i + 1;

                // find next reset
                int reset = getNextReset(text, marker);

                String substring = text.substring(marker, reset);
                Node[] children = this.parse0(substring);

                Hidden styleNode = new Hidden(children);

                nodes.add(styleNode);

                // jump marker to next style
                marker = reset;

                // jump to end of this style
                i = marker - 1;
                continue;
            }

            // TODO: should this be case sensitive?
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                int color = switch (Character.toLowerCase(c)) {
                    case COLOR_BLACK        -> 0x000000;
                    case COLOR_DARK_BLUE    -> 0x0000AA;
                    case COLOR_DARK_GREEN   -> 0x00AA00;
                    case COLOR_DARK_AQUA    -> 0x00AAAA;
                    case COLOR_DARK_RED     -> 0xAA0000;
                    case COLOR_DARK_PURPLE  -> 0xAA00AA;
                    case COLOR_GOLD         -> 0xFFAA00;
                    case COLOR_GRAY         -> 0xAAAAAA;
                    case COLOR_DARK_GRAY    -> 0x555555;
                    case COLOR_BLUE         -> 0x5555FF;
                    case COLOR_GREEN        -> 0x55FF55;
                    case COLOR_AQUA         -> 0x55FFFF;
                    case COLOR_RED          -> 0xFF5555;
                    case COLOR_LIGHT_PURPLE -> 0xFF55FF;
                    case COLOR_YELLOW       -> 0xFFFF55;
                    case COLOR_WHITE        -> 0xFFFFFF;
                    default -> throw new Error("Not implemented");
                };

                // leading text
                if (i - marker > 2) {
                    String leadingText = text.substring(marker, i - 1);
                    Text leadingTextNode = new Text(leadingText);
                    nodes.add(leadingTextNode);
                }

                marker = i + 1;

                // find next reset
                int reset = getNextReset(text, marker);

                String substring = text.substring(marker, reset);
                Node[] children = this.parse0(substring);

                Color styleNode = new Color(color, children);

                nodes.add(styleNode);

                // jump marker to next style
                marker = reset;

                // jump to end of this style
                i = marker - 1;
                continue;
            }

            if (c == RESET) {
                if (marker == i - 1) {
                    marker = i + 1;

                    // ignore reset (is respected by previous styles ending)
                    continue;
                }

                String leadingText = text.substring(marker, i - 1);
                Text leadingTextNode = new Text(leadingText);
                nodes.add(leadingTextNode);

                marker = i + 1;

                continue;
            }
        }

        // add trailing text as literal
        if (marker < text.length()) {
            String trailingText = text.substring(marker);
            Text trailingTextNode = new Text(trailingText);
            nodes.add(trailingTextNode);
        }

        return nodes.toArray(Node[]::new);
    }

    private int getNextReset(@NotNull String text, int start) {
        for (int j = start + 1; j < text.length(); j++) {
            char c1 = text.charAt(j);
            char c2 = text.charAt(j - 1);

            // continue if the current char is not a reset control char
            if (c1 != RESET) continue;

            // continue if the precious char is not an indicator
            if (c2 != indicator) continue;

            return j - 1;
        }

        // default: end of text
        return text.length();
    }

    private String parse0(@NotNull Node node) {
        StringBuilder builder = new StringBuilder();

        if (node instanceof Text text) {
            builder.append(text.getLiteral());
        }

        if (node instanceof Style style) {
            char token = switch (style.getType()) {
                case BOLD          -> STYLE_BOLD;
                case ITALICS       -> STYLE_ITALICS;
                case UNDERLINE     -> STYLE_UNDERLINE;
                case STRIKETHROUGH -> STYLE_STRIKETHROUGH;
            };

            builder.append(indicator);
            builder.append(token);

            for (Node child : node.getChildren())
                builder.append(this.parse0(child));
        }

        if (node instanceof Hidden) {
            builder.append(indicator);
            builder.append(STYLE_OBFUSCATE);

            for (Node child : node.getChildren())
                builder.append(this.parse0(child));
        }

        if (node instanceof Root) {
            for (Node child : node.getChildren()) {
                builder.append(indicator);
                builder.append(RESET);

                builder.append(this.parse0(child));
            }
        }

        return builder.toString();
    }
}
