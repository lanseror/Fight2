package com.fight2.entity.engine;

import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class F2CommonButton extends F2ButtonSprite {

    public F2CommonButton(final float pX, final float pY, final String text, final VertexBufferObjectManager vbom) {
        super(pX, pY, TextureFactory.getInstance().getAssetTextureRegion(TextureEnum.COMMON_BUTTON), vbom);
        final Font buttonTextFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final Text saveText = new Text(this.getWidth() * 0.5f, this.getHeight() * 0.5f, buttonTextFont, text, vbom);
        this.attachChild(saveText);
    }

}
