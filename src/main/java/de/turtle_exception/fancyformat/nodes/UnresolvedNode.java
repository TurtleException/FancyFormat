package de.turtle_exception.fancyformat.nodes;

import de.turtle_exception.fancyformat.Buffer;
import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.Node;
import de.turtle_exception.fancyformat.styles.CodeBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/** A node that has not yet been processed. Resolving this node may produce one or more nodes. */
public class UnresolvedNode extends Node {
    protected final @NotNull Node parent;
    protected final @NotNull String raw;
    protected final @NotNull Format<?> format;

    public UnresolvedNode(@NotNull Node parent, @NotNull String raw, @NotNull Format<?> format) {
        super(parent.getFormatter(), parent);
        this.parent = parent;
        this.raw = raw;
        this.format = format;
    }

    public void notifyParent() {
        parent.getChildren().add(this);
    }

    @Override
    public int resolve() {
        // replace self with identical TextNode if the parent is a code-block
        if (parent instanceof StyleNode sNode && sNode.getStyle().getClass().equals(CodeBlock.class)) {
            ArrayList<Node> siblings = parent.getChildren();
            for (int i = 0; i < siblings.size(); i++) {
                if (!siblings.get(i).equals(this)) continue;

                // replace self
                siblings.remove(i);
                siblings.add(i, new TextNode(parent, raw));

                return 1;
            }
        }

        Buffer buffer = format.newBuffer(parent, raw);
        List<Node> nodes = buffer.parse();

        ArrayList<Node> siblings = parent.getChildren();
        for (int i = 0; i < siblings.size(); i++) {
            if (!siblings.get(i).equals(this)) continue;

            // remove self
            siblings.remove(i);

            // add new siblings in reverse (because of right-shift insert)
            int j = nodes.size();
            while (j--> 0)
                siblings.add(i, nodes.get(j));

            break;
        }

        // resolve children of new siblings
        int i = nodes.size();
        for (Node node : nodes)
            i += node.resolve();
        return i;
    }
}
