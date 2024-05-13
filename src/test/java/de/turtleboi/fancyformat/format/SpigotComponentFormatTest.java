package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.node.Node;
import de.turtleboi.fancyformat.node.Root;
import de.turtleboi.fancyformat.node.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SpigotComponentFormatTest {
    private static final SpigotComponentFormat FORMAT = new SpigotComponentFormat();

    @Test
    void testInputLiteral() {
        TextComponent text = new TextComponent("Hello World");

        Root root = FORMAT.parseRoot(text);

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Text.class, node);
        assertEquals("Hello World", ((Text) node).getLiteral());
    }

    @Test
    void testOutputLiteral() {
        Text textNode = new Text("Hello World");

        BaseComponent text = FORMAT.parse(textNode);

        assertInstanceOf(TextComponent.class, text);
        assertEquals("Hello World", ((TextComponent) text).getText());
    }
}