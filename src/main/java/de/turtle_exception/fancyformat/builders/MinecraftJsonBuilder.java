package de.turtle_exception.fancyformat.builders;

import com.google.gson.*;
import de.turtle_exception.fancyformat.MessageBuilder;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import de.turtle_exception.fancyformat.nodes.ContentNode;
import de.turtle_exception.fancyformat.nodes.RootNode;
import de.turtle_exception.fancyformat.nodes.StyleNode;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.WrapperStyle;
import org.jetbrains.annotations.NotNull;

public class MinecraftJsonBuilder extends MessageBuilder {
    public MinecraftJsonBuilder(@NotNull Node node) {
        super(node);
    }

    @Override
    public @NotNull String build() {
        return getGson().toJson(this.buildJson());
    }

    public @NotNull JsonElement buildJson() {
        if (node instanceof ContentNode cNode)
            return new JsonPrimitive(cNode.getContent());

        // TODO: event node

        if (node instanceof RootNode rNode) {
            JsonArray arr = new JsonArray();
            for (Node child : rNode.getChildren())
                arr.add(new MinecraftJsonBuilder(child).buildJson());
            return arr;
        }

        // this should never be true
        if (!(node instanceof StyleNode sNode))
            return JsonNull.INSTANCE;

        Style style = sNode.getStyle();

        JsonObject json = new JsonObject();

        if (style instanceof WrapperStyle wStyle)
            json.addProperty(wStyle.getMinecraftName(), true);

        if (style instanceof Color cStyle)
            json.addProperty("color", cStyle.getName());

        // TODO: include comment styles according to FancyFormatter rules

        JsonArray extra = new JsonArray();
        for (Node child : node.getChildren())
            extra.add(new MinecraftJsonBuilder(child).buildJson());
        json.add("extra", extra);

        return json;
    }
}
