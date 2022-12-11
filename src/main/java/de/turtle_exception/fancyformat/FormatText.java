package de.turtle_exception.fancyformat;

import de.turtle_exception.fancyformat.nodes.MentionNode;
import de.turtle_exception.fancyformat.nodes.RootNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class FormatText {
    private final Node root;
    private final int size;

    private final HashMap<Format, String> stringCache = new HashMap<>();

    FormatText(@NotNull FancyFormatter formatter, @NotNull String content, @NotNull Format format) {
        this.root = new RootNode(formatter, content, format);
        this.size = this.root.resolve();
    }

    public synchronized @NotNull String toString(@NotNull Format format) {
        if (!stringCache.containsKey(format))
            stringCache.put(format, root.toString(format));
        return stringCache.get(format);
    }

    @Override
    public String toString() {
        return this.toString(Format.TURTLE);
    }

    /* - MENTIONS - */

    public @NotNull List<String> getMentions(@NotNull MentionType type) {
        return findAll(node -> (node instanceof MentionNode mNode) && mNode.getType().equals(type))
                .stream().map(node -> ((MentionNode) node).getContentRaw()).toList();
    }

    public boolean mentions(@NotNull MentionType type, @NotNull String mention) {
        return findFirst(node -> {
            if (!(node instanceof MentionNode mNode))
                return false;

            return mNode.getType().equals(type) && mNode.getContentRaw().equals(mention);
        }) != null;
    }

    public boolean mentionsEveryone() {
        return mentions(MentionType.USER, "everyone") || mentions(MentionType.USER_ALT, "everyone");
    }

    public boolean mentionsHere() {
        return mentions(MentionType.USER, "here") || mentions(MentionType.USER_ALT, "here");
    }

    /* - NODE TRAVERSAL - */

    protected @Nullable Node findFirst(@NotNull Predicate<Node> condition) {
        return findFirst(root, condition);
    }

    protected static @Nullable Node findFirst(@NotNull Node node, @NotNull Predicate<Node> condition) {
        if (condition.test(node)) return node;
        for (Node child : node.getChildren()) {
            Node r = findFirst(child, condition);
            if (r != null) return r;
        }
        return null;
    }

    protected @NotNull List<Node> findAll(@NotNull Predicate<Node> condition) {
        return findAll(root, condition, new ArrayList<>());
    }

    protected static @NotNull List<Node> findAll(@NotNull Node node, @NotNull Predicate<Node> condition, @NotNull List<Node> nodes) {
        if (condition.test(node)) {
            nodes.add(node);
            return nodes;
        }
        for (Node child : node.getChildren())
            findAll(child, condition, nodes);
        return nodes;
    }
}
