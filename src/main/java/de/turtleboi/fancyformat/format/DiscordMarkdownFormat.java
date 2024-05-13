package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.Format;
import de.turtleboi.fancyformat.node.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiscordMarkdownFormat extends Format<String> {
    @Override
    public @NotNull Node[] parse(String text) {
        ArrayList<Node> nodes = new ArrayList<>();

        String[] segments = text.split("\n");

        for (String segment : segments) {
            Node[] segmentNodes = this.parse0(segment);
            Collections.addAll(nodes, segmentNodes);
        }

        return nodes.toArray(Node[]::new);
    }

    @Override
    public String parse(@NotNull Node node) {
        return this.parse0(node);
    }

    private @NotNull Node[] parse0(String text) {
        if (text.isEmpty())
            return new Node[]{};

        List<Node> nodes = new ArrayList<>();

        SortedSet<BlockStyle> availableBlockStyles = new TreeSet<>((o1, o2) -> {
            int compareStart = Integer.compare(o1.start(), o2.start());
            if (compareStart != 0) return compareStart;
            int compareEnd = Integer.compare(o1.end(), o2.end());
            if (compareEnd != 0) return compareEnd;
            // match greedily (longer token takes precedent)
            return Integer.compare(o2.token().length(), o1.token().length());
        });

        parseStyle(text, "*", s -> new Style(Style.Type.ITALICS, this.parse0(s)), availableBlockStyles::add);
        parseStyle(text, "_", s -> new Style(Style.Type.ITALICS, this.parse0(s)), availableBlockStyles::add);
        parseStyle(text, "**", s -> new Style(Style.Type.BOLD, this.parse0(s)), availableBlockStyles::add);
        parseStyle(text, "__", s -> new Style(Style.Type.UNDERLINE, this.parse0(s)), availableBlockStyles::add);
        parseStyle(text, "~~", s -> new Style(Style.Type.STRIKETHROUGH, this.parse0(s)), availableBlockStyles::add);
        parseStyle(text, "||", s -> new Hidden(this.parse0(s)), availableBlockStyles::add);

        ArrayList<BlockStyle> outerBlockStyles = new ArrayList<>(availableBlockStyles.size());

        // filter out overlapping styles
        available:
        for (BlockStyle availableBlockStyle : availableBlockStyles) {
            for (BlockStyle outerBlockStyle : outerBlockStyles) {
                if (outerBlockStyle.end() > availableBlockStyle.start())
                    continue available;
            }

            outerBlockStyles.add(availableBlockStyle);
        }

        // no styles -> literal text only
        if (outerBlockStyles.isEmpty()) {
            return new Node[]{ new Text(text) };
        }

        for (int i = 0; i < outerBlockStyles.size(); i++) {
            BlockStyle style = outerBlockStyles.get(i);

            int   endOfLastStyle = 0;
            int startOfThisStyle = style.start() - style.token.length();

            if (i > 0) {
                BlockStyle prevStyle = outerBlockStyles.get(i - 1);
                endOfLastStyle = prevStyle.end() + prevStyle.token().length();
            }

            // try to insert the text between styles
            if (endOfLastStyle < startOfThisStyle) {
                // leading text
                String leadingText = text.substring(endOfLastStyle, startOfThisStyle + 1);
                nodes.add(new Text(leadingText));
            }

            String styledText = text.substring(style.start() + style.token().length(), style.end());
            Node styledNode = style.extractor().apply(styledText);
            nodes.add(styledNode);
        }

        // try to insert trailing text
        BlockStyle lastStyle = outerBlockStyles.get(outerBlockStyles.size() - 1);
        if (lastStyle.end() + lastStyle.token().length() < text.length()) {
            String trailingText = text.substring(lastStyle.end() + lastStyle.token().length());
            nodes.add(new Text(trailingText));
        }

        return nodes.toArray(Node[]::new);
    }

    private static void parseStyle(@NotNull String text, @NotNull String token, Function<String, Node> extractor, Consumer<BlockStyle> styleConsumer) {
        int l = token.length() + 1;

        int start;
        int   end = -l;
        do {
            start = text.indexOf(token, end + l);
            end = text.indexOf(token, start + l);

            if (start >= 0 && end >= 0) {
                BlockStyle blockStyle = new BlockStyle(token, start, end, extractor);
                styleConsumer.accept(blockStyle);
            }
        } while (start >= 0 && end >= 0);
    }

    private String parse0(@NotNull Node node) {
        StringBuilder builder = new StringBuilder();

        if (node instanceof Text text) {
            builder.append(text.getLiteral());
        }

        if (node instanceof Style style) {
            String token = switch (style.getType()) {
                case BOLD          -> "**";
                case ITALICS       -> "*";
                case UNDERLINE     -> "__";
                case STRIKETHROUGH -> "~~";
            };

            builder.append(token);

            for (Node child : node.getChildren())
                builder.append(this.parse0(child));

            builder.append(token);
        }

        if (node instanceof Hidden) {
            builder.append("||");

            for (Node child : node.getChildren())
                builder.append(this.parse0(child));

            builder.append("||");
        }

        if (node instanceof Root) {
            for (Node child : node.getChildren())
                builder.append(this.parse0(child));
        }

        return builder.toString();
    }

    private record BlockStyle(String token, int start, int end, Function<String, Node> extractor) { }
}
