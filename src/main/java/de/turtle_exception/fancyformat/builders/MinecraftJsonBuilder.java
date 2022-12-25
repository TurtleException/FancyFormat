package de.turtle_exception.fancyformat.builders;

import com.google.gson.*;
import de.turtle_exception.fancyformat.MessageBuilder;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import de.turtle_exception.fancyformat.nodes.*;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import de.turtle_exception.fancyformat.styles.Quote;
import de.turtle_exception.fancyformat.styles.VisualStyle;
import org.jetbrains.annotations.NotNull;

public class MinecraftJsonBuilder extends MessageBuilder<JsonElement> {
    public MinecraftJsonBuilder(@NotNull Node node) {
        super(node);
    }

    public @NotNull JsonElement build() {
        if (node instanceof TextNode cNode)
            return new JsonPrimitive(cNode.getContent());

        if (node instanceof RootNode rNode) {
            JsonArray arr = new JsonArray();
            for (Node child : rNode.getChildren())
                arr.add(new MinecraftJsonBuilder(child).build());
            return arr;
        }

        JsonObject json = new JsonObject();

        if (node instanceof ActionNode aNode)
            json.add(aNode.getType(), aNode.toJson());

        if (node instanceof MentionNode) {
            for (VisualStyle mentionStyle : node.getFormatter().getMentionStyles()) {
                if (mentionStyle instanceof Color cStyle)
                    json.addProperty("color", cStyle.getName());
                if (mentionStyle instanceof FormatStyle fStyle)
                    json.addProperty(fStyle.getName(), true);
            }
        }

        if (node instanceof StyleNode sNode) {
            Style style = sNode.getStyle();

            if (style instanceof Color cStyle)
                json.addProperty("color", cStyle.getName());

            if (style instanceof FormatStyle fStyle)
                json.addProperty(fStyle.getName(), true);

            if (style instanceof Quote) {
                for (VisualStyle quoteStyle : node.getFormatter().getQuoteStyles()) {
                    if (quoteStyle instanceof Color cStyle)
                        json.addProperty("color", cStyle.getName());
                    if (quoteStyle instanceof FormatStyle fStyle)
                        json.addProperty(fStyle.getName(), true);
                }
            }
        }

        JsonArray extra = new JsonArray();
        for (Node child : node.getChildren())
            extra.add(new MinecraftJsonBuilder(child).build());
        json.add("extra", extra);

        return json;
    }
}
