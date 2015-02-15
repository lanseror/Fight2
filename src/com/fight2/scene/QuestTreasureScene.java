package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.fight2.GameActivity;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.QuestResult;
import com.fight2.entity.QuestTile.TileItem;
import com.fight2.entity.engine.CardFrame;
import com.fight2.util.F2SoundManager;
import com.fight2.util.ResourceManager;

public class QuestTreasureScene extends BaseScene {
    private final static int CARD_WIDTH = 300;
    private final static int CARD_HEIGHT = 450;
    private final IEntity cardFrame;

    public QuestTreasureScene(final QuestResult questResult, final GameActivity activity) throws IOException {
        super(activity);
        this.cardFrame = new Rectangle(cameraCenterX, cameraCenterY, CARD_WIDTH, CARD_HEIGHT, vbom);
        cardFrame.setRotation(90);
        cardFrame.setAlpha(0);
        cardFrame.setScale(0.33f);
        final TileItem tileItem = questResult.getItem();

        this.attachChild(cardFrame);
        if (tileItem == TileItem.Card) {
            final Card card = questResult.getCard();
            final IEntity cardSprite = new CardFrame(CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f, CARD_WIDTH, CARD_HEIGHT, card, activity);
            cardFrame.attachChild(cardSprite);
        } else if (tileItem == TileItem.Stamina) {
            final TextureEnum staminaEnum = TextureEnum.COMMON_STAMINA;
            final IEntity staminaImg = createACImageSprite(staminaEnum, CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f);
            cardFrame.attachChild(staminaImg);
        } else if (tileItem == TileItem.Ticket) {
            final TextureEnum ticketEnum = TextureEnum.COMMON_ARENA_TICKET;
            final IEntity ticketImg = createACImageSprite(ticketEnum, CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f);
            cardFrame.attachChild(ticketImg);
        } else if (tileItem == TileItem.CoinBag) {
            final TextureEnum coinBagEnum = TextureEnum.COMMON_COIN_BAG;
            final IEntity coinBagImg = createACImageSprite(coinBagEnum, CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f);
            cardFrame.attachChild(coinBagImg);
        } else if (tileItem == TileItem.SummonCharm) {
            final TextureEnum itemEnum = TextureEnum.COMMON_SUMMON_CHARM;
            final IEntity itemImg = createACImageSprite(itemEnum, CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f);
            cardFrame.attachChild(itemImg);
        } else if (tileItem == TileItem.Diamon) {
            final TextureEnum itemEnum = TextureEnum.COMMON_DIAMOND;
            final IEntity itemImg = createACImageSprite(itemEnum, CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f);
            cardFrame.attachChild(itemImg);
        }

        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);
        this.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.isActionUp()) {
                    ResourceManager.getInstance().sceneBack();
                }
                return true;
            }

        });

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
        // playAnimation();
        activity.getGameHub().needSmallChatRoom(false);
    }

    @Override
    protected void playAnimation() {
        F2SoundManager.getInstance().play(SoundEnum.SUMMON);
        final IEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(0.3f, 0.33f, 1), new RotationByModifier(0.3f, 270));
        cardFrame.registerEntityModifier(modifier);
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}
