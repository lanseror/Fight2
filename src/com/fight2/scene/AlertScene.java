package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ResourceManager;

public class AlertScene extends BaseScene {
    private final String msg;

    public AlertScene(final GameActivity activity, final String msg) throws IOException {
        super(activity);
        this.msg = msg;
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

        final Font detailFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);

        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 420);
        final Text skillEffectText = new Text(frameSprite.getWidth() * 0.5f, 170, detailFont, msg, textOptions, vbom);
        this.topAlignEntity(skillEffectText, 200);
        frameSprite.attachChild(skillEffectText);

        final F2ButtonSprite confirmButton = createACF2CommonButton(frameSprite.getWidth() * 0.5f, 45, "确定");
        frameSprite.attachChild(confirmButton);
        this.registerTouchArea(confirmButton);
        confirmButton.setOnClickListener(new F2OnClickListener() {

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
