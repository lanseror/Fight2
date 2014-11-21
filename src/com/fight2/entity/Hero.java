package com.fight2.entity;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.TiledTextureEnum;
import com.fight2.util.TiledTextureFactory;

public class Hero extends AnimatedSprite {

    public Hero(final float x, final float y, final float width, final float height, final ITiledTextureRegion pTiledTextureRegion,
            final VertexBufferObjectManager vbom) {
        super(x, y, width, height, TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.PLAYER), vbom);
    }

}
