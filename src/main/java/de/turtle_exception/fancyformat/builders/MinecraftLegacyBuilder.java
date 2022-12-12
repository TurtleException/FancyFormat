package de.turtle_exception.fancyformat.builders;

import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.MessageBuilder;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import de.turtle_exception.fancyformat.nodes.MentionNode;
import de.turtle_exception.fancyformat.nodes.StyleNode;
import de.turtle_exception.fancyformat.nodes.TextNode;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MinecraftLegacyBuilder extends MessageBuilder {
    private final String reset = node.getFormatter().getMinecraftFormattingCode() + "r";

    public MinecraftLegacyBuilder(@NotNull Node node) {
        super(node);
    }

    @Override
    public @NotNull String build() {
        if (node instanceof TextNode tNode)
            return tNode.getContent();

        // TODO: Format according to FancyFormatter rules
        if (node instanceof MentionNode mNode)
            return mNode.parseDiscord();

        String prefix    = getFormat(node);
        String inherited = null;

        StringBuilder builder = new StringBuilder();

        ArrayList<Node> children = node.getChildren();
        for (int i = 0, l = children.size(); i < l; i++) {
            Node child = children.get(i);

            // inherit formats after the first child
            if (i > 0) {
                // load lazily
                if (inherited == null)
                    inherited = getInheritedFormats();

                builder.append(inherited);
            }

            builder.append(prefix);
            builder.append(child.toString(Format.MINECRAFT_LEGACY));

            if (!prefix.isEmpty())
                builder.append(reset);
        }

        return builder.toString();
    }

    private @NotNull String getInheritedFormats() {
        StringBuilder builder = new StringBuilder();

        Node current = node;
        while (current.getParent() != null) {
            current = current.getParent();

            builder.append(getFormat(current));
        }

        return builder.toString();
    }

    private static @NotNull String getFormat(@NotNull Node node) {
        if (node instanceof StyleNode sNode) {
            Style style = sNode.getStyle();

            if (style instanceof FormatStyle wStyle) {
                return ""
                        + node.getFormatter().getMinecraftFormattingCode()
                        + wStyle.getCode();
            }

            if (style instanceof Color cStyle) {
                return  ""
                        + node.getFormatter().getMinecraftFormattingCode()
                        + cStyle.getCode();
            }

            // TODO: format other styles according to FancyFormatter rules
        }

        return "";
    }
}
