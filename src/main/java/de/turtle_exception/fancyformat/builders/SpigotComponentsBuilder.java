package de.turtle_exception.fancyformat.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.turtle_exception.fancyformat.*;
import de.turtle_exception.fancyformat.nodes.*;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import de.turtle_exception.fancyformat.styles.Quote;
import de.turtle_exception.fancyformat.styles.VisualStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class SpigotComponentsBuilder extends MessageBuilder<BaseComponent[]> {
    public SpigotComponentsBuilder(@NotNull Node node) {
        super(node);
    }

    @Override
    public @NotNull BaseComponent @NotNull [] build() {
        if (node instanceof TextNode tNode)
            return new BaseComponent[]{ new TextComponent(tNode.getContent()) };

        if (node instanceof RootNode<?> rNode) {
            ArrayList<BaseComponent> list = new ArrayList<>();
            for (Node child : rNode.getChildren())
                list.addAll(Arrays.asList(new SpigotComponentsBuilder(child).build()));
            return list.toArray(new BaseComponent[0]);
        }

        TextComponent component = new TextComponent();

        if (node instanceof HoverNode hNode) {
            JsonElement contents = hNode.getContents();
            HoverEvent event = null;

            if (hNode.getAction().equalsIgnoreCase("show_text")) {
                // parse JSON content
                FormatText<JsonElement> content = node.getFormatter().newText(contents, Format.MINECRAFT_JSON);
                Text text = new Text(content.parse(Format.SPIGOT_COMPONENTS));

                event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
            } else if (hNode.getAction().equalsIgnoreCase("show_item")) {
                JsonObject obj;
                if (contents instanceof JsonArray arr)
                    obj = arr.get(0).getAsJsonObject();
                else
                    obj = contents.getAsJsonObject();

                String id = obj.get("id").getAsString();

                int count = 1;
                if (obj.has("count"))
                    count = obj.get("count").getAsInt();

                ItemTag tag = null;
                if (obj.has("tag"))
                    tag = ItemTag.ofNbt(obj.get("tag").getAsString());

                Item item = new Item(id, count, tag);
                event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, item);
            } else if (hNode.getAction().equalsIgnoreCase("show_entity")) {
                JsonObject obj;
                if (contents instanceof JsonArray arr)
                    obj = arr.get(0).getAsJsonObject();
                else
                    obj = contents.getAsJsonObject();

                String type = obj.get("type").getAsString();
                String id   = obj.get("id").getAsString();

                BaseComponent name = null;
                if (obj.has("name")) {
                    FormatText<JsonElement> content = node.getFormatter().newText(obj.get("name"), Format.MINECRAFT_JSON);
                    name = new TextComponent(content.parse(Format.SPIGOT_COMPONENTS));
                }

                Entity entity = new Entity(type, id, name);
                event = new HoverEvent(HoverEvent.Action.SHOW_ENTITY, entity);
            }

            component.setHoverEvent(event);
        }

        if (node instanceof ClickNode cNode) {
            ClickEvent.Action action = null;
            for (ClickEvent.Action value : ClickEvent.Action.values()) {
                if (cNode.getAction().equalsIgnoreCase(value.name())) {
                    action = value;
                    break;
                }
            }

            ClickEvent event = new ClickEvent(action, cNode.getValue());
            component.setClickEvent(event);
        }

        if (node instanceof MentionNode) {
            for (VisualStyle mentionStyle : node.getFormatter().getMentionStyles()) {
                if (mentionStyle instanceof Color cStyle)
                    component.setColor(ChatColor.getByChar(cStyle.getCode()));
                if (mentionStyle instanceof FormatStyle fStyle)
                    switch (fStyle.getName()) {
                        case "bold": component.setBold(true);
                        case "italic": component.setItalic(true);
                        case "underline": component.setUnderlined(true);
                        case "strikethrough": component.setStrikethrough(true);
                        case "obfuscated": component.setObfuscated(true);
                    }
            }
        }

        if (node instanceof StyleNode sNode) {
            Style style = sNode.getStyle();

            if (style instanceof Color cStyle)
                component.setColor(ChatColor.getByChar(cStyle.getCode()));

            if (style instanceof FormatStyle fStyle)
                switch (fStyle.getName()) {
                    case "bold": component.setBold(true);
                    case "italic": component.setItalic(true);
                    case "underline": component.setUnderlined(true);
                    case "strikethrough": component.setStrikethrough(true);
                    case "obfuscated": component.setObfuscated(true);
                }

            if (style instanceof Quote) {
                for (VisualStyle mentionStyle : node.getFormatter().getQuoteStyles()) {
                    if (mentionStyle instanceof Color cStyle)
                        component.setColor(ChatColor.getByChar(cStyle.getCode()));
                    if (mentionStyle instanceof FormatStyle fStyle)
                        switch (fStyle.getName()) {
                            case "bold": component.setBold(true);
                            case "italic": component.setItalic(true);
                            case "underline": component.setUnderlined(true);
                            case "strikethrough": component.setStrikethrough(true);
                            case "obfuscated": component.setObfuscated(true);
                        }
                }
            }
        }

        ArrayList<BaseComponent> extra = new ArrayList<>();
        for (Node child : node.getChildren())
            extra.addAll(Arrays.asList(new SpigotComponentsBuilder(child).build()));
        component.setExtra(extra);

        return new BaseComponent[]{ component };
    }
}
