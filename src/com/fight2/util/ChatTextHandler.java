package com.fight2.util;

import java.util.List;

import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ChatTextHandler {
    private static String FORMAT = "\n%s: %s";
    private final Text text;

    public ChatTextHandler(final Text sampleText, final VertexBufferObjectManager vbom) {
        super();
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, sampleText.getWidth() - 5);
        this.text = new Text(0, 0, sampleText.getFont(), sampleText.getText(), sampleText.getCharactersMaximum(), textOptions, vbom);
    }

    public String handle(final String sender, final String content) {
        text.setText(String.format(FORMAT, sender, content));
        final List<CharSequence> lines = text.getLines();
        final String line1 = lines.get(0).toString();
        if (lines.size() > 1) {
            return line1 + "...";
        } else {
            return line1;
        }

    }
}
