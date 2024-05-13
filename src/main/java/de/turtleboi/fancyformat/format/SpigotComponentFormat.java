package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.Format;
import de.turtleboi.fancyformat.node.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpigotComponentFormat extends Format<BaseComponent> {
    @Override
    public @NotNull Node[] parse(BaseComponent text) {
        return this.parse0(text);
    }

    @Override
    public BaseComponent parse(@NotNull Node node) {
        return this.parse0(node);
    }

    private @NotNull Node[] parse0(BaseComponent text) {
        ArrayList<Node> nodes = new ArrayList<>();

        if (text instanceof TextComponent textComponent) {
            Node node = new Text(textComponent.getText());

            Boolean italic = textComponent.isItalicRaw();
            if (italic != null && italic)
                node = new Style(Style.Type.ITALICS, node);

            Boolean bold = textComponent.isBoldRaw();
            if (bold != null && bold)
                node = new Style(Style.Type.BOLD, node);

            Boolean underlined = textComponent.isUnderlinedRaw();
            if (underlined != null && underlined)
                node = new Style(Style.Type.UNDERLINE, node);

            Boolean strikethrough = textComponent.isStrikethroughRaw();
            if (strikethrough != null && strikethrough)
                node = new Style(Style.Type.STRIKETHROUGH, node);

            Boolean obfuscated = textComponent.isObfuscatedRaw();
            if (obfuscated != null && obfuscated)
                node = new Hidden(node);

            ChatColor color = textComponent.getColorRaw();
            if (color != null) {
                java.awt.Color awtColor = color.getColor();
                node = new Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), node);
            }

            HoverEvent hoverEvent = textComponent.getHoverEvent();
            if (hoverEvent != null) {
                if (hoverEvent.getAction() != HoverEvent.Action.SHOW_TEXT)
                    throw new Error("Not implemented");

                ArrayList<Node> hoverNodes = new ArrayList<>();
                List<Content> contents = hoverEvent.getContents();

                for (Content content : contents) {
                    Object value = ((net.md_5.bungee.api.chat.hover.content.Text) content).getValue();

                    if (value instanceof BaseComponent baseComponentValue) {
                        Node[] contentNodes = this.parse0(baseComponentValue);
                        Collections.addAll(hoverNodes, contentNodes);
                    } else if (value instanceof String stringValue) {
                        hoverNodes.add(new Text(stringValue));
                    } else {
                        throw new Error("Not implemented");
                    }
                }

                node = new HoverText(hoverNodes, node);
            }

            // TODO: extra components

            nodes.add(node);
        } else {
            throw new Error("Not implemented");
        }

        return nodes.toArray(Node[]::new);
    }

    private BaseComponent parse0(@NotNull Node node) {
        TextComponent component = new TextComponent();

        if (node instanceof Text text) {
            component.setText(text.getLiteral());
            return component;
        }

        if (node instanceof Style style) {
            switch (style.getType()) {
                case BOLD          -> component.setBold(true);
                case ITALICS       -> component.setItalic(true);
                case UNDERLINE     -> component.setUnderlined(true);
                case STRIKETHROUGH -> component.setStrikethrough(true);
            }
        }

        if (node instanceof Hidden) {
            component.setObfuscated(true);
        }

        if (node instanceof Color color) {
            ChatColor chatColor = ChatColor.of(color.toAWT());
            component.setColor(chatColor);
        }

        if (node instanceof HoverText hoverText) {
            Node[] content = hoverText.getContent();

            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT);
            for (Node contentNode : content) {
                BaseComponent contentComponent = this.parse(contentNode);

                Content c = new net.md_5.bungee.api.chat.hover.content.Text(new BaseComponent[]{ contentComponent });
                hoverEvent.addContent(c);
            }

            component.setHoverEvent(hoverEvent);
        }

        for (Node child : node.getChildren())
            component.addExtra(this.parse0(child));

        return component;
    }
}
