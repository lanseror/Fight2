package com.fight2.entity.engine;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.util.EntityFactory;

public class DialogFrame extends Rectangle {
    private static final EntityFactory ET_FACTORY = EntityFactory.getInstance();
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;

    public DialogFrame(final float x, final float y, final float width, final float height, final GameActivity activity) {
        super(x, y, width, height, activity.getVertexBufferObjectManager());
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        this.setAlpha(0);
        final TextureEnum mainEnum = TextureEnum.DIALOG_FULL;
        final TextureEnum rightEnum = TextureEnum.DIALOG_RIGHT;
        final TextureEnum bottomEnum = TextureEnum.DIALOG_BOTTOM;
        final TextureEnum rightBottomEnum = TextureEnum.DIALOG_RIGHT_BOTTOM;

        final float mainWidth = width - rightEnum.getWidth();
        final float mainHeight = height - bottomEnum.getHeight();
        final IEntity main = new ClipEntity(mainWidth * 0.5f, height - mainHeight * 0.5f, mainWidth, mainHeight);
        final Sprite mainSprite = ET_FACTORY.createALBImageSprite(mainEnum, 0, mainHeight - mainEnum.getHeight());
        main.attachChild(mainSprite);
        this.attachChild(main);

        final float rightWidth = rightEnum.getWidth();
        final float rightHeight = height - rightBottomEnum.getHeight();
        final IEntity right = new ClipEntity(width - rightWidth * 0.5f, height - rightHeight * 0.5f, rightWidth, rightHeight);
        final Sprite rightSprite = ET_FACTORY.createALBImageSprite(rightEnum, 0, rightHeight - rightEnum.getHeight());
        right.attachChild(rightSprite);
        this.attachChild(right);

        final float bottomWidth = width - rightBottomEnum.getWidth();
        final float bottomHeight = bottomEnum.getHeight();
        final IEntity bottom = new ClipEntity(bottomWidth * 0.5f, bottomHeight * 0.5f, bottomWidth, bottomHeight);
        final Sprite bottomSprite = ET_FACTORY.createALBImageSprite(bottomEnum, 0, 0);
        bottom.attachChild(bottomSprite);
        this.attachChild(bottom);

        final Sprite rightBottomSprite = ET_FACTORY.createALBImageSprite(rightBottomEnum, width - rightBottomEnum.getWidth(), 0);
        this.attachChild(rightBottomSprite);

    }
}
