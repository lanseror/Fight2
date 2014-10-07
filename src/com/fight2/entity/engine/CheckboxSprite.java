package com.fight2.entity.engine;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.TextureEnum;
import com.fight2.util.BWShaderProgram;
import com.fight2.util.TextureFactory;

public class CheckboxSprite extends Sprite {
    private static TextureEnum CHECKBOX_ENUM = TextureEnum.COMMON_CHECKBOX_ON;
    private boolean checked;

    public CheckboxSprite(final float pX, final float pY, final VertexBufferObjectManager vbom) {
        this(pX, pY, true, vbom);
    }

    public CheckboxSprite(final float pX, final float pY, final boolean checked, final VertexBufferObjectManager vbom) {
        super(pX, pY, TextureFactory.getInstance().getAssetTextureRegion(CHECKBOX_ENUM), vbom);
        setChecked(checked);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(final boolean checked) {
        this.checked = checked;
        if (checked) {
            this.setShaderProgram(PositionColorTextureCoordinatesShaderProgram.getInstance());
        } else {
            this.setShaderProgram(BWShaderProgram.getInstance());
        }
    }

    public void switchCheckbox() {
        setChecked(!checked);
    }

}
