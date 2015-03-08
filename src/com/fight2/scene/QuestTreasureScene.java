package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
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
    private static final String MSG2 = "你为公会找到了一些资源，为表感谢，公会奖励了你%2s公会贡献币。(        +%s)";

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
        int guildContrib = 0;

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
                guildContrib = 10;
            } else if (tileItem == TileItem.PileOfDiamon) {
                final ITiledTextureRegion tiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.TREASURE_PILE_DIAMON);
                final AnimatedSprite treasureSprite = new AnimatedSprite(treasureX, treasureY, tiledTextureRegion, vbom);
                treasureSprite.animate(600, true);
                itemSprite = treasureSprite;
                guildContrib = 10;
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
                    case Wood:
                        textureEnum = TextureEnum.QUEST_TREASURE_WOOD_BIG;
                        guildContrib = 5;
                        break;
                    case Mineral:
                        textureEnum = TextureEnum.QUEST_TREASURE_MINERAL_BIG;
                        guildContrib = 5;
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
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 450);
        final String msg = tileItem.isInBox() ? MSG1 : String.format(MSG2, guildContrib, guildContrib);
        final Text descText = new Text(frame.getWidth() * 0.5f, 275, detailFont, msg, textOptions, vbom);
        descText.setColor(0XFF330504);
        frame.attachChild(descText);

        if (!tileItem.isInBox()) {
            final Sprite guildContribImg = createACImageSprite(TextureEnum.COMMON_GUILD_CONTRIBUTION, 393, 260);
            frame.attachChild(guildContribImg);
        }

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
    }

    @Override
    protected void playAnimation() {
        final SoundEnum soundEnum = questResult.getItem().isInBox() ? SoundEnum.TREASURE3 : SoundEnum.TREASURE1;
        F2SoundManager.getInstance().play(soundEnum);
    }

    @Override
    public void leaveScene() {

    }

}
