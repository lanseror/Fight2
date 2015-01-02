package com.fight2.entity.engine;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TickerText;
import org.andengine.entity.text.TickerText.TickerTextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.entity.Card;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.EntityFactory;
import com.fight2.util.EntityUtils;
import com.fight2.util.ResourceManager;

public class HeroDialogFrame extends DialogFrame {
    private static final EntityFactory ET_FACTORY = EntityFactory.getInstance();
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;

    public HeroDialogFrame(final float x, final float y, final float width, final float height, final GameActivity activity, final Card card,
            final String content) {
        super(x, y, width, height, activity);
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        this.setAlpha(0);

        final CardAvatar avatar = new CardAvatar(100, height - 80, 80, 80, card, activity);
        this.attachChild(avatar);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final Text nameText = new Text(250, height - 80, titleFont, card.getName(), vbom);
        nameText.setColor(0XFF330504);
        EntityUtils.leftAlignEntity(nameText, 160);
        EntityUtils.topAlignEntity(nameText, height - 30);
        this.attachChild(nameText);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final Text contentText = new TickerText(width * 0.5f, height * 0.5f, detailFont, content, new TickerTextOptions(HorizontalAlign.CENTER, 15), vbom);
        contentText.setColor(0XFF330504);
        this.attachChild(contentText);
        EntityUtils.leftAlignEntity(contentText, 160);
        EntityUtils.topAlignEntity(contentText, height - 65);
        final F2ButtonSprite confirmButton = ET_FACTORY.createACF2CommonButton(width * 0.5f, 50, "确定");
        this.attachChild(confirmButton);
        activity.getEngine().getScene().registerTouchArea(confirmButton);
        confirmButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                activity.runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.getEngine().getScene().unregisterTouchArea(confirmButton);
                        HeroDialogFrame.this.detachSelf();
                    }
                });
            }
        });
    }
}
