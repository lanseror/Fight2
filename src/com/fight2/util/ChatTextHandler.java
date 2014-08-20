package com.fight2.util;

import java.util.List;

import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.FontEnum;

public class ChatTextHandler {
    private static String FORMAT = "\n%s: %s";
    private static String SAMPLE_TEXT = "ABCDE: FGHIJKLMNOPWRSTUVWXYZ!abcdefghijklm.  ";
    private final VertexBufferObjectManager vbom;
    private final int charactersMaximum;

    public ChatTextHandler(final int charactersMaximum, final VertexBufferObjectManager vbom) {
        super();
        this.vbom = vbom;
        this.charactersMaximum = charactersMaximum;
    }

    public String handle(final String sender, final String content) {
        final Font chatHandlerFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);
        final Text sampleText = new Text(0, 0, chatHandlerFont, SAMPLE_TEXT, vbom);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, sampleText.getWidth() - 5);
        final Text text = new Text(0, 0, chatHandlerFont, sampleText.getText(), charactersMaximum, textOptions, vbom);
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
