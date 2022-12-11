package de.turtle_exception.fancyformat.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.turtle_exception.fancyformat.MessageBuilder;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.Style;
import de.turtle_exception.fancyformat.nodes.MentionNode;
import de.turtle_exception.fancyformat.nodes.RootNode;
import de.turtle_exception.fancyformat.nodes.StyleNode;
import de.turtle_exception.fancyformat.nodes.TextNode;
import de.turtle_exception.fancyformat.styles.CodeBlock;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.Quote;
import de.turtle_exception.fancyformat.styles.WrapperStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TurtleBuilder extends MessageBuilder {
    public TurtleBuilder(@NotNull Node node) {
        super(node);
    }

    @Override
    public @NotNull String build() {
        return getGson().toJson(this.buildJson());
    }

    public @NotNull JsonElement buildJson() {
        if (node instanceof RootNode rNode) {
            JsonArray arr = new JsonArray();
            for (Node child : rNode.getChildren())
                arr.add(new TurtleBuilder(child).buildJson());
            return arr;
        }

        JsonObject json = new JsonObject();

        if (node instanceof TextNode tNode)
            json.addProperty("text", tNode.getContent());

        if (node instanceof MentionNode mNode) {
            JsonObject mention = new JsonObject();

            mention.addProperty("type", mNode.getType().name());
            mention.addProperty("content", mNode.getContentRaw());

            json.add("mention", mention);
        }

        if (node instanceof StyleNode sNode) {
            Style style = sNode.getStyle();

            if (style instanceof WrapperStyle wStyle)
                json.addProperty("style", wStyle.getMinecraftName());

            if (style instanceof CodeBlock cStyle)
                json.addProperty("code-block", cStyle.name());

            if (style instanceof Color cStyle)
                json.addProperty("color", cStyle.getName());

            if (style instanceof Quote qStyle)
                json.addProperty("quote", qStyle.name());
        }

        ArrayList<Node> children = node.getChildren();
        if (!children.isEmpty()) {
            JsonArray childArr = new JsonArray();
            for (Node child : children)
                childArr.add(new TurtleBuilder(child).buildJson());
            json.add("children", childArr);
        }

        return json;
    }
}
