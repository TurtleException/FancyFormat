package de.turtle_exception.fancyformat.buffers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.turtle_exception.fancyformat.Buffer;
import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.nodes.*;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

// TODO: this is extremely inefficient
// TODO: this ignored a 'false' formatting override
public class MinecraftJsonBuffer extends Buffer {
    private JsonElement json;
    private JsonObject object;

    public MinecraftJsonBuffer(@NotNull Node parent, @NotNull String raw) {
        super(parent, raw);
    }

    @Override
    public @NotNull List<Node> parse() {
        json = getGson().fromJson(raw, JsonElement.class);

        if (json instanceof JsonPrimitive primitive)
            return List.of(new TextNode(parent, primitive.getAsString()));

        if (json instanceof JsonObject) {
            this.object = ((JsonObject) json);
        } else {
            return this.asText();
        }

        String color = getOptional(() -> object.get("color").getAsString());
        if (color != null) {
            for (Color value : Color.values()) {
                if (!color.equals(value.getName())) continue;

                StyleNode        styleNode = new StyleNode(parent, value);
                UnresolvedNode contentNode = new UnresolvedNode(styleNode, toReducedString("color"), Format.MINECRAFT_JSON);
                contentNode.notifyParent();

                return List.of(styleNode);
            }
        }

        for (FormatStyle value : FormatStyle.values()) {
            if (!isTrue(value.getName())) continue;

            StyleNode        styleNode = new StyleNode(parent, value);
            UnresolvedNode contentNode = new UnresolvedNode(styleNode, toReducedString(value.getName()), Format.MINECRAFT_JSON);
            contentNode.notifyParent();

            return List.of(styleNode);
        }

        JsonObject clickEvent = getOptional(() -> object.getAsJsonObject("clickEvent"));
        if (clickEvent != null) {
            ClickNode        clickNode = new ClickNode(parent, clickEvent.get("action").getAsString(), clickEvent.get("value").getAsString());
            UnresolvedNode contentNode = new UnresolvedNode(clickNode, toReducedString("clickEvent"), Format.MINECRAFT_JSON);
            contentNode.notifyParent();

            return List.of(clickNode);
        }

        JsonObject hoverEvent = getOptional(() -> object.getAsJsonObject("hoverEvent"));
        if (hoverEvent != null) {
            HoverNode        hoverNode = new HoverNode(parent, hoverEvent.get("action").getAsString(), hoverEvent.getAsJsonObject("contents"));
            UnresolvedNode contentNode = new UnresolvedNode(hoverNode, toReducedString("hoverEvent"), Format.MINECRAFT_JSON);
            contentNode.notifyParent();

            return List.of(hoverNode);
        }

        // CONTENT & CHILDREN

        ArrayList<Node> nodes = new ArrayList<>();

        String text = getOptional(() -> object.get("text").getAsString());
        if (text != null)
            nodes.add(new TextNode(parent, text));

        JsonArray extra = getOptional(() -> object.getAsJsonArray("extra"));
        if (extra != null) {
            for (JsonElement child : extra) {
                UnresolvedNode node = new UnresolvedNode(parent, child.toString(), Format.MINECRAFT_JSON);
                node.notifyParent();

                nodes.add(node);
            }
        }

        return nodes;
    }

    private <T> @Nullable T getOptional(@NotNull Callable<T> c) {
        try {
            return c.call();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isTrue(@NotNull String key) {
        if (!object.has(key)) return false;

        try {
            return object.get(key).getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    private @NotNull String toReducedString(@NotNull String key) {
        object.remove(key);
        return object.toString();
    }
}
