package de.turtleboi.fancyformat.format;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.turtleboi.fancyformat.node.Node;
import de.turtleboi.fancyformat.node.Root;
import de.turtleboi.fancyformat.node.Text;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinecraftJSONFormatTest {
    private static final MinecraftJSONFormat FORMAT = new MinecraftJSONFormat();

    @Test
    void testInputString() {
        Root root = FORMAT.parseRoot("\"Hello World\"");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Text.class, node);
        assertEquals("Hello World", ((Text) node).getLiteral());
    }

    @Test
    void testInputJson() {
        Root root = FORMAT.parseRoot("{\"text\": \"Hello World\"}");

        assertEquals(1, root.getChildren().length);
        Node node = root.getChildren()[0];

        assertInstanceOf(Text.class, node);
        assertEquals("Hello World", ((Text) node).getLiteral());
    }

    @Test
    void testOutputJson() {
        Text node = new Text("Hello World");

        JsonElement text = FORMAT.parse(node);

        assertInstanceOf(JsonObject.class, text);

        JsonObject textObject = (JsonObject) text;

        assertTrue(textObject.has("text"));

        JsonElement textElement = textObject.get("text");

        assertInstanceOf(JsonPrimitive.class, textElement);
        assertEquals("Hello World", textElement.getAsString());
    }
}