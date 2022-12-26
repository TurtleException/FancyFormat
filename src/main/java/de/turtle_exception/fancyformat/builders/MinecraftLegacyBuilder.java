package de.turtle_exception.fancyformat.builders;

import de.turtle_exception.fancyformat.MessageBuilder;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import de.turtle_exception.fancyformat.formats.MinecraftLegacyFormat;
import de.turtle_exception.fancyformat.nodes.MentionNode;
import de.turtle_exception.fancyformat.nodes.StyleNode;
import de.turtle_exception.fancyformat.nodes.TextNode;
import de.turtle_exception.fancyformat.styles.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MinecraftLegacyBuilder extends MessageBuilder<String> {
    private final String reset = node.getFormatter().getMinecraftFormattingCode() + "r";

    public MinecraftLegacyBuilder(@NotNull Node node) {
        super(node);
    }

    @Override
    public @NotNull String build() {
        if (node instanceof TextNode tNode)
            return tNode.getContent();

        if (node instanceof MentionNode mNode) {
            StringBuilder builder = new StringBuilder();

            for (VisualStyle style : node.getFormatter().getMentionStyles()) {
                builder.append(node.getFormatter().getMinecraftFormattingCode());
                builder.append(style.getCode());
            }

            return builder.append(mNode.getContent()).toString();
        }

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
            builder.append(child.toString(MinecraftLegacyFormat.get()));

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

            if (style instanceof CodeBlock) {
                StringBuilder builder = new StringBuilder();

                for (VisualStyle codeStyle : node.getFormatter().getCodeBlockStyles()) {
                    builder.append(node.getFormatter().getMinecraftFormattingCode());
                    builder.append(codeStyle);
                }

                return builder.toString();
            }

            if (style instanceof Quote) {
                StringBuilder builder = new StringBuilder();

                for (VisualStyle quoteStyle : node.getFormatter().getQuoteStyles()) {
                    builder.append(node.getFormatter().getMinecraftFormattingCode());
                    builder.append(quoteStyle);
                }

                return builder.toString();
            }

            // TODO: format other styles according to FancyFormatter rules
        }

        return "";
    }
}
