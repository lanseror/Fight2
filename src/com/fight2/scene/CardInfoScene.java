package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardInfoScene extends BaseScene {
    private final static int CARD_WIDTH = 310;
    private final static int CARD_HEIGHT = 465;
    private final static int FRAME_BOTTOM = 80;
    private final Text nameTitleText;
    private final Text nameText;
    private final TextureFactory textureFactory = TextureFactory.getInstance();
    private Sprite cardSprite;
    private final Card card;

    public CardInfoScene(final GameActivity activity, final Card card) throws IOException {
        super(activity);
        this.card = card;
        final Font font = ResourceManager.getInstance().newFont(FontEnum.Default);
        nameTitleText = new Text(80, 426, font, "名称：", vbom);
        nameText = new Text(265, 426, font, session.getName(), 30, vbom);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                back();
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final Sprite infoFrame = createALBImageSprite(TextureEnum.CARDINFO_FRAME, this.simulatedLeftX + 420, FRAME_BOTTOM);
        this.attachChild(infoFrame);
        infoFrame.attachChild(nameTitleText);
        infoFrame.attachChild(nameText);

        final IEntity nameTouchArea = new Rectangle(450, 430, 70, 50, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    return true;
                }
                return false;
            }
        };
        nameTouchArea.setAlpha(0);
        infoFrame.attachChild(nameTouchArea);
        this.registerTouchArea(nameTouchArea);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        activity.getGameHub().needSmallChatRoom(false);
        final ITextureRegion texture = textureFactory.getTextureRegion(card.getImage());
        if (cardSprite != null) {
            cardSprite.detachSelf();
        }
        cardSprite = new Sprite(this.simulatedLeftX + 108 + CARD_WIDTH * 0.5f, FRAME_BOTTOM + CARD_HEIGHT * 0.5f, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
        this.attachChild(cardSprite);
    }

    @Override
    public void updateScene() {

    }

    @Override
    public void leaveScene() {
    }

}
