package de.turtle_exception.fancyformat.buffers;

import com.google.gson.*;
import de.turtle_exception.fancyformat.*;
import de.turtle_exception.fancyformat.formats.MinecraftJsonFormat;
import de.turtle_exception.fancyformat.formats.SpigotComponentsFormat;
import de.turtle_exception.fancyformat.nodes.*;
import de.turtle_exception.fancyformat.styles.Color;
import de.turtle_exception.fancyformat.styles.FormatStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpigotComponentsBuffer extends Buffer<BaseComponent[]> {
    public SpigotComponentsBuffer(@NotNull Node parent, @NotNull BaseComponent[] raw) {
        super(parent, raw, SpigotComponentsFormat.get());
    }

    @Override
    public @NotNull List<Node> parse() {
        if (raw.length == 0)
            return List.of();

        if (raw.length > 1) {
            ArrayList<Node> nodes = new ArrayList<>();

            for (BaseComponent component : raw) {
                UnresolvedNode<BaseComponent[]> node = new UnresolvedNode<>(parent, new BaseComponent[]{ component }, SpigotComponentsFormat.get());
                node.notifyParent();

                nodes.add(node);
            }

            return nodes;
        }

        BaseComponent component = raw[0];

        ChatColor color = component.getColorRaw();
        if (color != null) {
            for (Color value : Color.values()) {
                if (!color.getName().equals(value.getName())) continue;

                BaseComponent duplicate = component.duplicate();
                duplicate.setColor(null);

                StyleNode styleNode = new StyleNode(parent, value);
                UnresolvedNode<BaseComponent[]> contentNode = new UnresolvedNode<>(styleNode, new BaseComponent[]{ duplicate }, SpigotComponentsFormat.get());
                contentNode.notifyParent();

                return List.of(styleNode);
            }

            // TODO: hex colors
        }

        // TODO: handle false overrides

        Boolean bold = component.isBoldRaw();
        if (bold != null && bold)
            return handleFormat(component, FormatStyle.BOLD, c -> c.setBold(null));

        Boolean italic = component.isItalicRaw();
        if (italic != null && italic)
            return handleFormat(component, FormatStyle.ITALICS, c -> c.setItalic(null));

        Boolean underline = component.isUnderlinedRaw();
        if (underline != null && underline)
            return handleFormat(component, FormatStyle.UNDERLINE, c -> c.setUnderlined(null));

        Boolean strikethrough = component.isStrikethroughRaw();
        if (strikethrough != null && strikethrough)
            return handleFormat(component, FormatStyle.STRIKETHROUGH, c -> c.setStrikethrough(null));

        Boolean spoiler = component.isObfuscatedRaw();
        if (spoiler != null && spoiler)
            return handleFormat(component, FormatStyle.SPOILER, c -> c.setObfuscated(null));

        ClickEvent clickEvent = component.getClickEvent();
        if (clickEvent != null) {
            BaseComponent duplicate = component.duplicate();
            duplicate.setClickEvent(null);

            ClickNode clickNode = new ClickNode(parent, clickEvent.getAction().name().toLowerCase(), clickEvent.getValue());
            UnresolvedNode<BaseComponent[]> contentNode = new UnresolvedNode<>(clickNode, new BaseComponent[]{ duplicate }, SpigotComponentsFormat.get());
            contentNode.notifyParent();

            return List.of(clickNode);
        }

        HoverEvent hoverEvent = component.getHoverEvent();
        if (hoverEvent != null) {
            BaseComponent duplicate = component.duplicate();
            duplicate.setHoverEvent(null);

            List<Content> contentsRaw = hoverEvent.getContents();
            JsonElement contents = null;

            if (hoverEvent.getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
                JsonArray arr = new JsonArray();

                for (Content content : contentsRaw) {
                    Object value = ((Text) content).getValue();

                    if (value instanceof BaseComponent comp) {
                        FormatText contentText = parent.getFormatter().fromFormat(new BaseComponent[]{ comp }, SpigotComponentsFormat.get());

                        arr.add(contentText.parse(MinecraftJsonFormat.get()));
                    }

                    if (value instanceof String str)
                        arr.add(new JsonPrimitive(str));
                }

                contents = arr;
            }

            if (hoverEvent.getAction().equals(HoverEvent.Action.SHOW_ITEM)) {
                JsonObject obj = new JsonObject();

                Item item = ((Item) contentsRaw.get(0));

                obj.addProperty("id", item.getId());

                if (item.getCount() != -1)
                    obj.addProperty("count", item.getCount());

                if (item.getTag() != null)
                    obj.addProperty("tag", item.getTag().getNbt());

                contents = obj;
            }

            if (hoverEvent.getAction().equals(HoverEvent.Action.SHOW_ENTITY)) {
                JsonObject obj = new JsonObject();

                Entity entity = (Entity) contentsRaw.get(0);

                obj.addProperty("type", entity.getType());
                obj.addProperty("id", entity.getId());

                if (entity.getName() != null) {
                    FormatText name = parent.getFormatter().fromFormat(new BaseComponent[]{ entity.getName() }, SpigotComponentsFormat.get());
                    obj.add("name", name.parse(MinecraftJsonFormat.get()));
                }

                contents = obj;
            }

            if (contents == null)
                throw new AssertionError("Unsupported HoverAction type: " + hoverEvent.getAction());

            HoverNode hoverNode = new HoverNode(parent, hoverEvent.getAction().name().toLowerCase(), contents);
            UnresolvedNode<BaseComponent[]> contentNode = new UnresolvedNode<>(hoverNode, new BaseComponent[]{ duplicate }, SpigotComponentsFormat.get());
            contentNode.notifyParent();

            return List.of(hoverNode);
        }

        // CONTENT & CHILDREN

        ArrayList<Node> nodes = new ArrayList<>();

        if (component instanceof TextComponent tComp) {
            String text = tComp.getText();
            nodes.add(new TextNode(parent, text));
        }

        if (component.getExtra() != null) {
            for (BaseComponent extra : component.getExtra()) {
                UnresolvedNode<BaseComponent[]> node = new UnresolvedNode<>(parent, new BaseComponent[]{ extra }, SpigotComponentsFormat.get());
                node.notifyParent();

                nodes.add(node);
            }
        }

        // remove first node if it's an empty TextNode
        if (nodes.size() > 1)
            if (nodes.get(0) instanceof TextNode tNode && tNode.getContent().isEmpty())
                nodes.remove(0);

        return nodes;
    }

    private @NotNull List<Node> handleFormat(@NotNull BaseComponent component, @NotNull FormatStyle style, @NotNull Consumer<BaseComponent> setter) {
        BaseComponent duplicate = component.duplicate();
        setter.accept(duplicate);

        StyleNode styleNode = new StyleNode(parent, style);
        UnresolvedNode<BaseComponent[]> contentNode = new UnresolvedNode<>(styleNode, new BaseComponent[]{ duplicate }, SpigotComponentsFormat.get());
        contentNode.notifyParent();

        return List.of(styleNode);
    }
}
