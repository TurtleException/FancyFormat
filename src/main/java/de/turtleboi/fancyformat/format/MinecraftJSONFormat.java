package de.turtleboi.fancyformat.format;

import com.google.gson.*;
import de.turtleboi.fancyformat.Format;
import de.turtleboi.fancyformat.node.Node;
import de.turtleboi.fancyformat.node.Root;
import de.turtleboi.fancyformat.node.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinecraftJSONFormat extends Format<JsonElement> {
    private final Gson gson = new GsonBuilder().create();

    public @NotNull Node[] parse(String text) {
        JsonElement json = gson.fromJson(text, JsonElement.class);
        return this.parse(json);
    }

    public @NotNull Root parseRoot(String text) {
        return new Root(this.parse(text));
    }

    @Override
    public @NotNull Node[] parse(JsonElement text) {
        return this.parse0(text);
    }

    @Override
    public JsonElement parse(@NotNull Node node) {
        return this.parse0(node);
    }

    private @NotNull Node[] parse0(JsonElement text) {
        List<Node> nodes = new ArrayList<>();

        if (text instanceof JsonPrimitive jsonPrimitive) {
            nodes.add(new Text(jsonPrimitive.getAsString()));
        }

        if (text instanceof JsonArray jsonArray) {
            for (JsonElement element : jsonArray)
                Collections.addAll(nodes, this.parse0(element));
        }

        if (text instanceof JsonObject jsonObject) {
            JsonElement textElement = jsonObject.get("text");

            if (textElement == null)
                throw new Error("Not implemented");

            Text node = new Text(textElement.getAsString());

            nodes.add(node);
        }

        return nodes.toArray(Node[]::new);
    }

    private JsonElement parse0(@NotNull Node node) {
        if (node instanceof Text text) {
            JsonObject json = new JsonObject();
            json.addProperty("text", text.getLiteral());
            return json;
        }

        if (node instanceof Root) {
            JsonArray json = new JsonArray();

            for (Node child : node.getChildren())
                json.add(this.parse0(child));

            if (json.size() == 1)
                return json.get(0);
            return json;
        }

        throw new Error("Not implemented");
    }
}
