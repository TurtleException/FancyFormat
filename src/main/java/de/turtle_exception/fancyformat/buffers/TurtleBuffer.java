package de.turtle_exception.fancyformat.buffers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.turtle_exception.fancyformat.*;
import de.turtle_exception.fancyformat.nodes.*;
import de.turtle_exception.fancyformat.styles.CodeBlock;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.Quote;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TurtleBuffer extends Buffer<JsonElement> {
    public TurtleBuffer(@NotNull Node parent, @NotNull JsonElement raw) {
        super(parent, raw, Format.TURTLE);
    }

    @Override
    public @NotNull List<Node> parse() {
        if (raw instanceof JsonArray arr) {
            ArrayList<Node> nodes = new ArrayList<>();

            for (JsonElement element : arr) {
                UnresolvedNode<JsonElement> node = new UnresolvedNode<>(parent, element, Format.TURTLE);
                node.notifyParent();

                nodes.add(node);
            }

            return nodes;
        }

        if (!(raw instanceof JsonObject object))
            throw new AssertionError("Cannot comprehend " + raw.getClass().getSimpleName());

        String text = getOptional(() -> object.get("text").getAsString());
        if (text != null)
            return List.of(new TextNode(parent, text));

        JsonObject mention = getOptional(() -> object.getAsJsonObject("mention"));
        if (mention != null) {
            String type    = mention.get("type").getAsString();
            String content = mention.get("content").getAsString();

            for (MentionType value : MentionType.values())
                if (type.equals(value.name()))
                    return List.of(new MentionNode(parent, value, content));

            throw new IllegalArgumentException("Illegal mention type: " + type);
        }

        String styleStr  = getOptional(() -> object.get("style").getAsString());
        String codeBlock = getOptional(() -> object.get("code-block").getAsString());
        String color     = getOptional(() -> object.get("color").getAsString());
        String quote     = getOptional(() -> object.get("quote").getAsString());

        Style style = null;

        if (styleStr != null) {
            for (FormatStyle value : FormatStyle.values()) {
                if (styleStr.equals(value.getName())) {
                    style = value;
                    break;
                }
            }
        }

        if (codeBlock != null) {
            for (CodeBlock value : CodeBlock.values()) {
                if (codeBlock.equals(value.name())) {
                    style = value;
                    break;
                }
            }
        }

        if (color != null) {
            for (Color value : Color.values()) {
                if (color.equals(value.getName())) {
                    style = value;
                    break;
                }
            }
        }

        if (quote != null) {
            for (Quote value : Quote.values()) {
                if (quote.equals(value.name())) {
                    style = value;
                    break;
                }
            }
        }

        // this should never be true
        if (style == null)
            return this.asText();

        StyleNode styleNode = new StyleNode(parent, style);

        JsonArray children = getOptional(() -> object.getAsJsonArray("children"));
        if (children != null) {
            for (JsonElement child : children) {
                UnresolvedNode<JsonElement> node = new UnresolvedNode<>(styleNode, child, Format.TURTLE);
                node.notifyParent();
            }
        }

        return List.of(styleNode);
    }

    private <T> @Nullable T getOptional(@NotNull Callable<T> c) {
        try {
            return c.call();
        } catch (Exception e) {
            return null;
        }
    }
}
