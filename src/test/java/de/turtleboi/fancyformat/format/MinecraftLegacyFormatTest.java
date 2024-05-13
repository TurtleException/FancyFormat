package de.turtleboi.fancyformat.format;

import de.turtleboi.fancyformat.node.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinecraftLegacyFormatTest {
    private static final MinecraftLegacyFormat FORMAT = MinecraftLegacyFormat.createDefault();

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
        Root root = FORMAT.parseRoot("§lHello World");

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

        assertEquals("§lHello World", text);
    }

    @Test
    void testInputItalics() {
        Node root = FORMAT.parseRoot("§oHello World");

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

        assertEquals("§oHello World", text);
    }

    @Test
    void testInputUnderline() {
        Node root = FORMAT.parseRoot("§nHello World");

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

        assertEquals("§nHello World", text);
    }

    @Test
    void testInputStrikethrough() {
        Node root = FORMAT.parseRoot("§mHello World");

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

        assertEquals("§mHello World", text);
    }

    @Test
    void testInputObfuscated() {
        Node root = FORMAT.parseRoot("§kHello World");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Hidden.class, node);
    }

    @Test
    void testOutputObfuscated() {
        Text textNode = new Text("Hello World");
        Hidden hiddenNode = new Hidden(textNode);

        String text = FORMAT.parse(hiddenNode);

        assertEquals("§kHello World", text);
    }

    @Test
    void testInputReset() {
        Root root = FORMAT.parseRoot("§lHello §rWorld");

        assertEquals(2, root.getChildren().length);
        Node node = root.getChildren()[1];

        assertInstanceOf(Text.class, node);
        assertEquals("World", ((Text) node).getLiteral());
    }

    @Test
    void testOutputReset() {
        Text text0 = new Text("Hello ");
        Text text1 = new Text("World");
        Style style = new Style(Style.Type.BOLD, text0);

        Root root = new Root(style, text1);

        String text = FORMAT.parse(root);

        assertEquals("§lHello §rWorld", text);
    }

    @Test
    void testInputFormatOffset() {
        Root root = FORMAT.parseRoot("Hello §lWorld");

        assertEquals(2, root.getChildren().length);

        assertInstanceOf(Text.class, root.getChildren()[0]);
        assertInstanceOf(Style.class, root.getChildren()[1]);
    }

    @Test
    void testOutputFormatOffset() {
        Text text0 = new Text("Hello ");
        Text text1 = new Text("World");
        Style style = new Style(Style.Type.BOLD, text1);

        Root root = new Root(text0, style);

        String text = FORMAT.parse(root);

        assertEquals("Hello §lWorld", text);
    }

    @Test
    void testInputNestedFormat() {
        Root root = FORMAT.parseRoot("§lHello §oWorld");

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

        assertEquals("§lHello §oWorld", text);
    }

    @Test
    void testInputChainedFormat() {
        Root root = FORMAT.parseRoot("§lHello §rthere §lWorld");

        assertEquals(3, root.getChildren().length);

        Node helloStyle = root.getChildren()[0];

        assertInstanceOf(Style.class, helloStyle);
        assertTrue(((Style) helloStyle).isBold());

        assertEquals(1, helloStyle.getChildren().length);

        Node helloText = helloStyle.getChildren()[0];

        assertInstanceOf(Text.class, helloText);
        assertEquals("Hello ", ((Text) helloText).getLiteral());

        Node thereText = root.getChildren()[1];

        assertInstanceOf(Text.class, thereText);
        assertEquals("there ", ((Text) thereText).getLiteral());

        Node worldStyle = root.getChildren()[2];

        assertInstanceOf(Style.class, worldStyle);
        assertTrue(((Style) worldStyle).isBold());

        assertEquals(1, worldStyle.getChildren().length);

        Node worldText = worldStyle.getChildren()[0];

        assertInstanceOf(Text.class, worldText);
        assertEquals("World", ((Text) worldText).getLiteral());
    }

    @Test
    void testOutputChainedFormat() {
        Text text0 = new Text("Hello ");
        Text text1 = new Text("there ");
        Text text2 = new Text("World");

        Style style0 = new Style(Style.Type.BOLD, text0);
        Style style2 = new Style(Style.Type.BOLD, text2);

        Root root = new Root(style0, text1, style2);

        String text = FORMAT.parse(root);

        assertEquals("§lHello §rthere §lWorld", text);
    }

    @Test
    void doNotLeadWithReset() {
        Root root = FORMAT.parseRoot("Hello §rWorld");

        String text = FORMAT.parse(root);

        assertEquals("Hello World", text);
    }

    @Test
    void doNotRepeatReset() {
        Root root = FORMAT.parseRoot("§lHello §r§rWorld");

        String text = FORMAT.parse(root);

        assertEquals("§lHello §rWorld", text);
    }
}