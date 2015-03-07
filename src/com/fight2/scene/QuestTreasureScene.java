package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.engine.CardFrame;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.quest.QuestResult;
import com.fight2.entity.quest.QuestTile.TileItem;
import com.fight2.util.F2SoundManager;
import com.fight2.util.IParamCallback;
import com.fight2.util.ResourceManager;
import com.fight2.util.TiledTextureFactory;

public class QuestTreasureScene extends BaseScene {
    private final static int CARD_WIDTH = 100;
    private final static int CARD_HEIGHT = 150;
    private final QuestResult questResult;
    private static final String MSG1 = "找到一个宝箱，你打开宝箱，发现了一些财宝。";
    private static final String MSG2 = "仔细搜索之后，你发现了一些资源。";

    public QuestTreasureScene(final QuestResult questResult, final GameActivity activity) throws IOException {
        super(activity);
        this.questResult = questResult;
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.3f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final DialogFrame frame = new DialogFrame(cameraCenterX, cameraCenterY, 600, 350, activity);
        frame.bind(this, new IParamCallback() {
            @Override
            public void onCallback(final Object param) {
                back();
            }
        });

        final float treasureX = 300;
        final float treasureY = 180;

        final TileItem tileItem = questResult.getItem();
        final IEntity itemSprite;
        if (tileItem == TileItem.Card) {
            final Card card = questResult.getCard();
            itemSprite = new CardFrame(treasureX, treasureY, CARD_WIDTH, CARD_HEIGHT, card, activity);
        } else {
            if (tileItem == TileItem.Crystal) {
                final ITiledTextureRegion tiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.TREASURE_CRYSTAL);
                final AnimatedSprite treasureSprite = new AnimatedSprite(treasureX, treasureY, tiledTextureRegion, vbom);
                treasureSprite.animate(500, true);
                itemSprite = treasureSprite;
            } else {
                final TextureEnum textureEnum;
                switch (tileItem) {
                    case Stamina:
                        textureEnum = TextureEnum.COMMON_STAMINA;
                        break;
                    case Ticket:
                        textureEnum = TextureEnum.COMMON_ARENA_TICKET;
                        break;
                    case CoinBag:
                        textureEnum = TextureEnum.COMMON_COIN_BAG;
                        break;
                    case SummonCharm:
                        textureEnum = TextureEnum.COMMON_SUMMON_CHARM;
                        break;
                    case Diamon:
                        textureEnum = TextureEnum.COMMON_DIAMOND;
                        break;
                    case PileOfDiamon:
                        textureEnum = TextureEnum.QUEST_TREASURE_PILE_DIAMON;
                        break;
                    case Wood:
                        textureEnum = TextureEnum.QUEST_TREASURE_WOOD_BIG;
                        break;
                    case Mineral:
                        textureEnum = TextureEnum.QUEST_TREASURE_MINERAL_BIG;
                        break;
                    default:
                        textureEnum = null;
                        break;
                }
                itemSprite = createACImageSprite(textureEnum, treasureX, treasureY);
            }
        }
        frame.attachChild(itemSprite);

        final Font amountFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28, 128);
        final Text amountText = new Text(treasureX, treasureY, amountFont, String.format("×%s", 1), vbom);
        amountText.setColor(0XFF330504);
        amountText.setX(treasureX + itemSprite.getWidth() * 0.5f + 50);
        frame.attachChild(amountText);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 430);
        final String msg = tileItem.isInBox() ? MSG1 : MSG2;
        final Text descText = new Text(frame.getWidth() * 0.5f, 270, detailFont, msg, textOptions, vbom);
        descText.setColor(0XFF330504);
        frame.attachChild(descText);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
    }

    @Override
    protected void playAnimation() {
        F2SoundManager.getInstance().play(SoundEnum.TREASURE1);
    }

    @Override
    public void leaveScene() {

    }

}
