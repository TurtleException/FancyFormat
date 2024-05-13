package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.node.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscordMarkdownFormatTest {
    private static final DiscordMarkdownFormat FORMAT = new DiscordMarkdownFormat();

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
    void testInputBold() {
        Root root = FORMAT.parseRoot("**Hello World**");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Style.class, node);
        assertTrue(((Style) node).isBold());
    }

    @Test
    void testOutputBold() {
        Text textNode = new Text("Hello World");
        Style styleNode = new Style(Style.Type.BOLD, textNode);

        String text = FORMAT.parse(styleNode);

        assertEquals("**Hello World**", text);
    }

    @Test
    void testInputItalics() {
        Node root = FORMAT.parseRoot("*Hello World*");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Style.class, node);
        assertTrue(((Style) node).isItalic());
    }

    @Test
    void testInputItalicsAlt() {
        Node root = FORMAT.parseRoot("_Hello World_");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Style.class, node);
        assertTrue(((Style) node).isItalic());
    }

    @Test
    void testOutputItalics() {
        Text textNode = new Text("Hello World");
        Style styleNode = new Style(Style.Type.ITALICS, textNode);

        String text = FORMAT.parse(styleNode);

        assertEquals("*Hello World*", text);
    }

    @Test
    void testInputUnderline() {
        Node root = FORMAT.parseRoot("__Hello World__");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Style.class, node);
        assertTrue(((Style) node).isUnderlined());
    }

    @Test
    void testOutputUnderline() {
        Text textNode = new Text("Hello World");
        Style styleNode = new Style(Style.Type.UNDERLINE, textNode);

        String text = FORMAT.parse(styleNode);

        assertEquals("__Hello World__", text);
    }

    @Test
    void testInputStrikethrough() {
        Node root = FORMAT.parseRoot("~~Hello World~~");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Style.class, node);
        assertTrue(((Style) node).isStrikethrough());
    }

    @Test
    void testOutputStrikethrough() {
        Text textNode = new Text("Hello World");
        Style styleNode = new Style(Style.Type.STRIKETHROUGH, textNode);

        String text = FORMAT.parse(styleNode);

        assertEquals("~~Hello World~~", text);
    }

    @Test
    void testInputSpoiler() {
        Node root = FORMAT.parseRoot("||Hello World||");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Hidden.class, node);
    }

    @Test
    void testOutputSpoiler() {
        Text textNode = new Text("Hello World");
        Hidden hiddenNode = new Hidden(textNode);

        String text = FORMAT.parse(hiddenNode);

        assertEquals("||Hello World||", text);
    }

    @Test
    void testInputNestedFormat() {
        Root root = FORMAT.parseRoot("**Hello _World_**");

        assertEquals(1, root.getChildren().length);

        Node level1Node = root.getChildren()[0];

        assertInstanceOf(Style.class, level1Node);
        assertTrue(((Style) level1Node).isBold());

        assertEquals(2, level1Node.getChildren().length);

        Node textHello = level1Node.getChildren()[0];
        Node level2Node = level1Node.getChildren()[1];

        assertInstanceOf(Text.class, textHello);
        assertEquals("Hello ", ((Text) textHello).getLiteral());

        assertInstanceOf(Style.class, level2Node);
        assertTrue(((Style) level2Node).isItalic());
        assertEquals(1, level2Node.getChildren().length);

        Node textWorld = level2Node.getChildren()[0];

        assertInstanceOf(Text.class, textWorld);
        assertEquals("World", ((Text) textWorld).getLiteral());
    }

    @Test
    void testOutputNestedFormat() {
        Text text0 = new Text("Hello ");
        Text text1 = new Text("World");

        Style style1 = new Style(Style.Type.ITALICS, text1);
        Style style0 = new Style(Style.Type.BOLD, text0, style1);

        Root root = new Root(style0);

        String text = FORMAT.parse(root);

        assertEquals("**Hello *World***", text);
    }

    @Test
    void testInputInterlinked() {
        Root root = FORMAT.parseRoot("**Hello _there** World_");

        assertEquals(2, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Style.class, node);
        assertTrue(((Style) node).isBold());
    }

    @Test
    void testBreakAtNewline() {
        Root root = FORMAT.parseRoot("**Hello\nWorld**");

        assertEquals(2, root.getChildren().length);
        Node node0 = root.getChildren()[0];
        Node node1 = root.getChildren()[1];

        assertInstanceOf(Text.class, node0);
        assertInstanceOf(Text.class, node1);

        assertEquals("**Hello", ((Text) node0).getLiteral());
        assertEquals("World**", ((Text) node1).getLiteral());
    }
}