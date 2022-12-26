package de.turtle_exception.fancyformat;

import com.google.gson.JsonElement;
import de.turtle_exception.fancyformat.formats.TurtleFormat;
import de.turtle_exception.fancyformat.nodes.MentionNode;
import de.turtle_exception.fancyformat.nodes.RootNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/**
 * A FormatText is an abstract representation of a text that has been parsed from a formatted text. It can be used to
 * translate that text into any supported {@link Format}.
 * @see FancyFormatter#fromFormat(Object, Format)
 */
@SuppressWarnings("unused")
public class FormatText {
    private final Node root;
    private final int size;

    private final HashMap<Format<?>, Object> cache = new HashMap<>();

    /**
     * You're probably looking for this:
     * <pre> {@code
     * // reuse this formatter for other texts
     * FancyFormatter formatter = new FancyFormatter();
     * FormatText text = formatter.from(content, format);
     * } </pre>
     */
    <T> FormatText(@NotNull FancyFormatter formatter, @NotNull T content, @NotNull Format<T> format) {
        this.root = new RootNode<>(formatter, content, format);
        this.size = this.root.resolve();
    }

    @SuppressWarnings("unchecked")
    public synchronized <U> @NotNull U parse(@NotNull Format<U> format) {
        if (!cache.containsKey(format))
            cache.put(format, root.parse(format));
        return (U) cache.get(format);
    }

    public JsonElement parse() {
        return this.parse(TurtleFormat.get());
    }

    /**
     * Returns this text in the provided {@link Format}.
     * @param format Desired text format.
     * @return String representation of this text in the desired format.
     */
    public <U> @NotNull String toString(@NotNull Format<U> format) {
        return format.makeString(this.parse(format));
    }

    /** Returns this text in the {@link TurtleFormat}. */
    @Override
    public String toString() {
        return this.toString(TurtleFormat.get());
    }

    /* - MENTIONS - */

    /**
     * Provides a List of all mentions of type {@code type} this text contains. Mentions are returned as Strings,
     * representing the raw content of a mention (without brackets or identifiers).
     * @param type Type of mentions
     * @return List of mentions of type {@code type}.
     */
    public @NotNull List<String> getMentions(@NotNull MentionType type) {
        return findAll(node -> (node instanceof MentionNode mNode) && mNode.getType().equals(type))
                .stream().map(node -> ((MentionNode) node).parseDiscord()).toList();
    }

    /**
     * Provides a List of all mentions this text contains. Mentions are returned as Strings, formatted as Discord
     * markdown. See <a href="https://discord.com/developers/docs/reference#message-formatting-formats">
     * Discord Developer Portal (Documentation): API Reference - Message Formatting</a> for more info.
     * @return List of all mentions this text contains.
     */
    public @NotNull List<String> getMentions() {
        return findAll(node -> (node instanceof MentionNode)).stream().map(node -> ((MentionNode) node).parseDiscord()).toList();
    }

    /**
     * Returns {@code true} if this text contains a mention of type {@code type} and with the raw content of
     * {@code mention}.
     * @param type Type of the mention.
     * @param mention Raw content of the mention (without brackets or identifiers).
     * @return true, if the desired mention exists in this text.
     */
    public boolean mentions(@NotNull MentionType type, @NotNull String mention) {
        return findFirst(node -> {
            if (!(node instanceof MentionNode mNode))
                return false;

            return mNode.getType().equals(type) && mNode.getContentRaw().equals(mention);
        }) != null;
    }

    /**
     * Returns {@code true} if this text contains an "@everyone" mention.
     * @return true, if this text mentions @everyone
     * @see FormatText#mentionsHere()
     */
    public boolean mentionsEveryone() {
        return mentions(MentionType.USER, "everyone") || mentions(MentionType.USER_ALT, "everyone");
    }

    /**
     * Returns {@code true} if this text contains a "@here" mention.
     * @return true, if this text mentions @here
     * @see FormatText#mentionsEveryone()
     */
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
