package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.node.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PlainTextFormatTest {
    private static final PlainTextFormat FORMAT = new PlainTextFormat();

    @Test
    void testInputLiteral() {
        Root root = FORMAT.parseRoot("Hello World");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Text.class, node);
        assertEquals("Hello World", ((Text) node).getLiteral());
    }

    @Test
    void testOutputLiteral() {
        Text textNode = new Text("Hello World");

        String text = FORMAT.parse(textNode);

        assertEquals("Hello World", text);
    }

    @Test
    void testIgnoreStyle() {
        Text textNode = new Text("Hello World");
        Style styleNode = new Style(Style.Type.BOLD, textNode);

        String text = FORMAT.parse(styleNode);

        assertEquals("Hello World", text);
    }

    @Test
    void testIgnoreColor() {
        Text textNode = new Text("Hello World");
        Color colorNode = new Color(255, 0, 0, textNode);

        String text = FORMAT.parse(colorNode);

        assertEquals("Hello World", text);
    }

    @Test
    void testIgnoreHidden() {
        Text textNode = new Text("Hello World");
        Hidden hiddenNode = new Hidden(textNode);

        String text = FORMAT.parse(hiddenNode);

        assertEquals("Hello World", text);
    }
}