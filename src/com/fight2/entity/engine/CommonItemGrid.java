package com.fight2.entity.engine;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.util.EntityFactory;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CommonItemGrid extends Sprite {
    private static final TextureEnum TEXTURE_ENUM = TextureEnum.COMMON_ITEM_GRID;
    private final Font itemFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
    private final Font amountFont = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
    private final Text text;

    public CommonItemGrid(final float pX, final float pY, final VertexBufferObjectManager vbom, final TextureEnum itemEnum, final int amount) {
        super(pX, pY, TextureFactory.getInstance().getAssetTextureRegion(TEXTURE_ENUM), vbom);
        final float itemX = this.mWidth * 0.5f;
        final float itemY = this.mHeight * 0.5f;
        final IEntity itemImg = EntityFactory.getInstance().createACImageSprite(itemEnum, itemX, itemY);
        this.attachChild(itemImg);

        final Text itemText = new Text(itemX, this.mHeight + 25, itemFont, "竞技场门票", vbom);
        itemText.setColor(0XFFFFE8C6);
        this.text = new Text(itemX, this.mHeight + 25, amountFont, String.valueOf(amount), vbom);
        this.attachChild(text);
    }

}