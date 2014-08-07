package com.fight2.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class BattlePartyFrame extends Sprite {
    public BattlePartyFrame(final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
            final boolean isBottom) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    }

}
