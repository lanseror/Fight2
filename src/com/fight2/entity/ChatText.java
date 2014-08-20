package com.fight2.entity;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.text.vbo.ITextVertexBufferObject;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.IFont;

public class ChatText extends Text {

    public ChatText(final float pX, final float pY, final IFont pFont, final CharSequence pText, final int pCharactersMaximum, final TextOptions pTextOptions,
            final ITextVertexBufferObject pTextVertexBufferObject) {
        super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions, pTextVertexBufferObject);
        // TODO Auto-generated constructor stub
    }

}
