package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.Format;
import de.turtleboi.fancyformat.node.Node;
import de.turtleboi.fancyformat.node.Text;
import org.jetbrains.annotations.NotNull;

public class PlainTextFormat extends Format<String> {
    @Override
    public @NotNull Node[] parse(String text) {
        return new Text[]{ new Text(text) };
    }

    @Override
    public String parse(@NotNull Node node) {
        if (node instanceof Text text)
            return text.getLiteral();

        StringBuilder builder = new StringBuilder();

        for (Node child : node.getChildren())
            builder.append(this.parse(child));

        return builder.toString();
    }
}
