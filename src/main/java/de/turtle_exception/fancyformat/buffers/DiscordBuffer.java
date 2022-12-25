package de.turtle_exception.fancyformat.buffers;

import de.turtle_exception.fancyformat.*;
import de.turtle_exception.fancyformat.nodes.MentionNode;
import de.turtle_exception.fancyformat.nodes.StyleNode;
import de.turtle_exception.fancyformat.nodes.UnresolvedNode;
import de.turtle_exception.fancyformat.styles.CodeBlock;
import de.turtle_exception.fancyformat.styles.Quote;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;

public class DiscordBuffer extends Buffer<String> {
    private final StyleMarker markerBold          = new StyleMarker('*', 2, FormatStyle.BOLD);
    private final StyleMarker markerItalicsStar   = new StyleMarker('*', 1, FormatStyle.ITALICS);
    private final StyleMarker markerUnderline     = new StyleMarker('_', 2, FormatStyle.UNDERLINE);
    private final StyleMarker markerItalicsLine   = new StyleMarker('_', 1, FormatStyle.ITALICS_ALT);
    private final StyleMarker markerStrikethrough = new StyleMarker('~', 2, FormatStyle.STRIKETHROUGH);
    private final StyleMarker markerSpoiler       = new StyleMarker('|', 2, FormatStyle.SPOILER);
    private final StyleMarker markerCodeBlock     = new StyleMarker('`', 3, CodeBlock.BLOCK);
    private final StyleMarker markerCodeInline    = new StyleMarker('`', 1, CodeBlock.INLINE);

    private final StyleMarker[] markers = {
            markerBold, markerItalicsStar, markerUnderline, markerItalicsLine,
            markerStrikethrough, markerSpoiler, markerCodeBlock, markerCodeInline
    };

    private int mdMentionStart = -1;
    private int mdMentionEnd   = -1;

    private final char[] chars = raw.toCharArray();
    private int index = -1; // incremented before first iteration
    private int line = 0;

    private boolean done = false;

    public DiscordBuffer(@NotNull Node parent, @NotNull String raw) {
        super(parent, raw, Format.DISCORD);
    }

    public @NotNull List<Node> parse() {
        if (done)
            throw new IllegalStateException("May not reuse DiscordBuffer!");
        this.done = true;

        // check for single line comment
        if (raw.startsWith("> ")) {
            int linebreak = raw.indexOf("\n");
            String   line = raw.substring(2, linebreak != -1 ? linebreak : raw.length());

            StyleNode              commentNode = new StyleNode(parent, Quote.SINGLE_LINE);
            UnresolvedNode<String> contentNode = new UnresolvedNode<>(commentNode, line, Format.DISCORD);
            contentNode.notifyParent();

            return List.of(commentNode);
        }

        // check for multi line comment
        if (raw.startsWith(">>> ")) {
            StyleNode              commentNode = new StyleNode(parent, Quote.MULTI_LINE);
            UnresolvedNode<String> contentNode = new UnresolvedNode<>(commentNode, raw.substring(4), Format.DISCORD);
            contentNode.notifyParent();

            return List.of(commentNode);
        }

        outer:
        while (++index < chars.length) {
            char c = chars[index];

            // TODO: formats should break on newlines
            if (c == '\n')
                line++;

            // TODO: this will ignore an actual mention with something like "some <fake-mention> and <@4563546> stuff"

            // check for mention (start)
            if (c == '<') {
                // don't set the start after the end
                if (mdMentionEnd == -1)
                    mdMentionStart = index;
                continue;
            }

            // check for mention (end)
            if (c == '>') {
                // mention may not end before it started
                if (mdMentionStart != -1)
                    mdMentionEnd = index;
                continue;
            }

            // check wrapping markdown characters
            for (StyleMarker marker : markers) {
                // skip if the char doesn't match
                if (c != marker.mdChar) continue;

                // skip if multiple chars are required but not provided
                if (marker.length > 1 && !isMulti(marker.length)) continue;

                // set start / end index (depending on which one is not yet set)
                if (marker.startIndex == -1) {
                    marker.startIndex = index;
                    continue outer;
                }

                // guard: start & end have to be separated by at least one char
                if (index <= marker.startIndex + 1) continue outer;

                // mark end index as current index
                if (marker.endIndex == -1) {
                    marker.endIndex = index;

                    // check if this style starts before any other style (indicating that it is the outer format)
                    for (StyleMarker marker1 : markers) {
                        // skip if that format has not started yet
                        if (marker1.startIndex == -1) continue;

                        // check if startIndex is lower than the current one
                        if (marker1.startIndex < marker.startIndex)
                            continue outer;
                    }

                    // no format with a lower startIndex found -> this is the one
                    break outer;
                }
            }
        }

        // determine outer format marker (lowest index)
        StyleMarker outerFormat = this.getOuterFormat();

        // BUILD NODES

        String str1; // everything BEFORE the md part
        String str2; // the md part (without md chars)
        String str3; // everything AFTER the md part

        if (outerFormat != null) {
            str1 = raw.substring(0, outerFormat.startIndex);
            str2 = raw.substring(outerFormat.startIndex + outerFormat.length, outerFormat.endIndex);
            str3 = raw.substring(outerFormat.endIndex + outerFormat.length);

            StyleNode                styleNode = new StyleNode(parent, outerFormat.style);
            UnresolvedNode<String> contentNode = new UnresolvedNode<>(styleNode, str2, Format.DISCORD);
            contentNode.notifyParent();

            ArrayList<Node> nodes = new ArrayList<>();

            if (!str1.isEmpty() && !str1.isBlank())
                nodes.add(new UnresolvedNode<>(parent, str1, Format.DISCORD));

            nodes.add(styleNode);

            if (!str3.isEmpty() && !str3.isBlank())
                nodes.add(new UnresolvedNode<>(parent, str3, Format.DISCORD));

            return nodes;
        }

        // mentions are checked after wrapper formats because they cannot nest any other formats

        if (mdMentionEnd != -1) {
            str1 = raw.substring(0, mdMentionStart);
            str2 = raw.substring(mdMentionStart + 1, mdMentionEnd);
            str3 = raw.substring(mdMentionEnd + 1);

            MentionType type = MentionType.fromRawMention(str2);

            if (type == null)
                return asText();

            MentionNode mentionNode = new MentionNode(parent, type, str2.substring(type.getPrefixLength()));

            ArrayList<Node> nodes = new ArrayList<>();

            if (!str1.isEmpty() && !str1.isBlank())
                nodes.add(new UnresolvedNode<>(parent, str1, Format.DISCORD));

            nodes.add(mentionNode);

            if (!str3.isEmpty() && !str3.isBlank())
                nodes.add(new UnresolvedNode<>(parent, str3, Format.DISCORD));

            return nodes;
        }

        // if none of the above applies, this represents a text node
        return asText();
    }

    /* - - - */

    private boolean isMulti(int length) {
        if ((index + (length - 1)) >= chars.length)
            return false;

        for (int i = 0; i < length; i++)
            if (chars[index + i] != chars[index])
                return false;
        return true;
    }

    private StyleMarker getOuterFormat() {
        StyleMarker outerFormat = null;
        for (StyleMarker marker : markers) {
            // skip: no occurrence
            if (marker.startIndex == -1) continue;
            if (marker.endIndex   == -1) continue;

            if (outerFormat == null) {
                outerFormat = marker;
                continue;
            }

            if (marker.startIndex < outerFormat.startIndex) {
                outerFormat = marker;
            }
        }
        return outerFormat;
    }

    /* - - - */

    private static class StyleMarker {
        final char mdChar;
        final int length;
        final Style style;

        int startIndex = -1;
        int   endIndex = -1;

        private StyleMarker(char mdChar, @Range(from = 1, to = Integer.MAX_VALUE) int length, Style style) {
            this.mdChar = mdChar;
            this.length = length;
            this.style = style;
        }
    }
}
