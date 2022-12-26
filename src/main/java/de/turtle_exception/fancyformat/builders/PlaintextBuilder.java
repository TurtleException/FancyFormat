package de.turtle_exception.fancyformat.builders;

import de.turtle_exception.fancyformat.MessageBuilder;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.formats.PlaintextFormat;
import de.turtle_exception.fancyformat.nodes.ContentNode;
import org.jetbrains.annotations.NotNull;

public class PlaintextBuilder extends MessageBuilder<String> {
    public PlaintextBuilder(@NotNull Node node) {
        super(node);
    }

    @Override
    public @NotNull String build() {
        if (node instanceof ContentNode cNode)
            return cNode.getContent();

        StringBuilder builder = new StringBuilder();

        for (Node child : node.getChildren())
            builder.append(child.toString(PlaintextFormat.get()));

        return builder.toString();
    }
}
