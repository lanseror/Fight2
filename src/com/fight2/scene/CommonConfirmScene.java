package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ICallback;

public class CommonConfirmScene extends BaseScene {
    private final ICallback yesCallback;

    public CommonConfirmScene(final GameActivity activity, final ICallback yesCallback) throws IOException {
        super(activity);
        this.yesCallback = yesCallback;
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.65f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final Sprite frameSprite = createACImageSprite(TextureEnum.COMMON_ALERT_FRAME, cameraCenterX, cameraCenterY);
        this.attachChild(frameSprite);

        final F2ButtonSprite confirmButton = createACF2CommonButton(frameSprite.getWidth() * 0.5f - 100, 45, "确定");
        frameSprite.attachChild(confirmButton);
        this.registerTouchArea(confirmButton);
        confirmButton.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                yesCallback.onCallback();
                back();
            }
        });

        final F2ButtonSprite cancelButton = createACF2CommonButton(frameSprite.getWidth() * 0.5f + 100, 45, "取消");
        frameSprite.attachChild(cancelButton);
        this.registerTouchArea(cancelButton);
        cancelButton.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                back();
            }
        });

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
        // TODO Auto-generated method stub
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}
