package de.turtle_exception.fancyformat.nodes;

import de.turtle_exception.fancyformat.FancyFormatter;
import de.turtle_exception.fancyformat.MentionType;
import de.turtle_exception.fancyformat.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/** A node that contains a mention. */
public class MentionNode extends Node implements ContentNode {
    protected final @NotNull MentionType type;
    protected final @NotNull String content;

    public MentionNode(@NotNull FancyFormatter formatter, @Nullable Node parent, @NotNull MentionType type, @NotNull String content) {
        super(formatter, parent);
        this.type = type;
        this.content = content;
    }

    public MentionNode(@NotNull Node parent, @NotNull MentionType type, @NotNull String content) {
        this(parent.getFormatter(), parent, type, content);
    }

    public @NotNull MentionType getType() {
        return type;
    }

    @Override
    public @NotNull String getContent() {
        // TODO: MentionPolicy
        return content;
    }

    public @NotNull String getContentRaw() {
        return content;
    }

    public @NotNull String parseDiscord() {
        return "<" + content + ">";
    }

    @Override
    public final @NotNull ArrayList<Node> getChildren() {
        // A MentionNode may not have children
        return new ArrayList<>();
    }
}
