package de.turtle_exception.fancyformat.builders;

import de.turtle_exception.fancyformat.MessageBuilder;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import de.turtle_exception.fancyformat.formats.DiscordFormat;
import de.turtle_exception.fancyformat.nodes.MentionNode;
import de.turtle_exception.fancyformat.nodes.StyleNode;
import de.turtle_exception.fancyformat.nodes.TextNode;
import de.turtle_exception.fancyformat.styles.CodeBlock;
import de.turtle_exception.fancyformat.styles.Quote;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import org.jetbrains.annotations.NotNull;

public class DiscordBuilder extends MessageBuilder<String> {
    public DiscordBuilder(@NotNull Node node) {
        super(node);
    }

    @Override
    public @NotNull String build() {
        if (node instanceof TextNode tNode)
            return tNode.getContent();

        if (node instanceof MentionNode mNode)
            return mNode.parseDiscord();

        StringBuilder builder = new StringBuilder();

        for (Node child : node.getChildren())
            builder.append(child.toString(DiscordFormat.get()));

        if (node instanceof StyleNode sNode) {
            Style style = sNode.getStyle();

            if (style instanceof FormatStyle wStyle) {
                builder.insert(0, wStyle.getWrapper());
                builder.append(wStyle.getWrapper());
            }

            if (style instanceof Quote) {
                builder.insert(0, "> ");
            }

            if (style instanceof CodeBlock cStyle) {
                if (cStyle == CodeBlock.BLOCK) {
                    builder.insert(0, "```");
                    builder.append("```");
                } else if (cStyle == CodeBlock.INLINE) {
                    builder.insert(0, "`");
                    builder.append("`");
                }
            }
        }

        return builder.toString();
    }
}
