package de.turtle_exception.fancyformat.nodes;

import de.turtle_exception.fancyformat.FancyFormatter;
import de.turtle_exception.fancyformat.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/** A node that contains raw text. */
public class TextNode extends Node implements ContentNode {
    protected final @NotNull String content;

    public TextNode(@NotNull FancyFormatter formatter, @Nullable Node parent, @NotNull String content) {
        super(formatter, parent);
        this.content = content;
    }

    public TextNode(@NotNull Node parent, @NotNull String content) {
        this(parent.getFormatter(), parent, content);
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public final @NotNull ArrayList<Node> getChildren() {
        // A TextNode may not have children
        return new ArrayList<>();
    }
}
