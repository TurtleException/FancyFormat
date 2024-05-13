package de.turtleboi.fancyformat.format;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.turtleboi.fancyformat.Format;
import de.turtleboi.fancyformat.node.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FancyFormat extends Format<JsonElement> {
    @Override
    public @NotNull Node[] parse(JsonElement text) {
        return new Node[]{ this.parse0(text) };
    }

    @Override
    public JsonObject parse(@NotNull Node node) {
        return this.parse0(node);
    }

    public @NotNull Node parse0(JsonElement text) {
        if (text == null)
            throw new IllegalArgumentException("Text may not be null");
        if (!(text instanceof JsonObject json))
            throw new IllegalArgumentException("Text must be a JsonObject");

        String type = json.get("type").getAsString();

        Node[] children = null;
        if (!type.equals("text")) {
            JsonArray childArray = json.getAsJsonArray("children");
            ArrayList<Node> nodes = new ArrayList<>(childArray.size());
            for (JsonElement child : childArray)
                nodes.add(this.parse0(child));
            children = nodes.toArray(Node[]::new);
        }

        return switch (type) {
            case "root" -> new Root(children);
            case "text" -> {
                String literal = json.get("text").getAsString();
                yield new Text(literal);
            }
            case "style" -> {
                String styleStr = json.get("style").getAsString();
                Style.Type style = Style.Type.valueOf(styleStr);
                yield new Style(style, children);
            }
            case "color" -> {
                JsonObject color = json.getAsJsonObject("color");
                int r = color.get("r").getAsInt();
                int g = color.get("g").getAsInt();
                int b = color.get("b").getAsInt();
                yield new Color(r, g, b, children);
            }
            case "hidden" -> new Hidden(children);
            case "hoverText" -> {
                JsonArray contentArray = json.getAsJsonArray("content");
                ArrayList<Node> content = new ArrayList<>(contentArray.size());
                for (JsonElement node : contentArray)
                    content.add(this.parse0(node));
                yield  new HoverText(content, children);
            }
            default -> throw new Error("Not implemented");
        };
    }

    public JsonObject parse0(@NotNull Node node) {
        JsonObject json = new JsonObject();

        if (node instanceof Root) {
            json.addProperty("type", "root");
        }

        if (node instanceof Text text) {
            json.addProperty("type", "text");
            json.addProperty("text", text.getLiteral());
        }

        if (node instanceof Style style) {
            json.addProperty("type", "style");
            json.addProperty("style", style.getType().name());
        }

        if (node instanceof Color color) {
            json.addProperty("type", "color");
            JsonObject colorJson = new JsonObject();
            colorJson.addProperty("r", color.getRed());
            colorJson.addProperty("g", color.getGreen());
            colorJson.addProperty("b", color.getBlue());
            json.add("color", colorJson);
        }

        if (node instanceof Hidden) {
            json.addProperty("type", "hidden");
        }

        if (node instanceof HoverText hoverText) {
            json.addProperty("type", "hoverText");
            JsonArray content = new JsonArray();
            for (Node n : hoverText.getContent())
                content.add(this.parse(n));
            json.add("content", content);
        }

        JsonArray children = new JsonArray();
        for (Node child : node.getChildren())
            children.add(this.parse0(child));
        if (!children.isEmpty())
            json.add("children", children);

        return json;
    }
}
