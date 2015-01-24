package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.algorithm.collision.EntityCollisionChecker;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.engine.CardAvatar;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.CardUtils;
import com.fight2.util.IRCallback;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class PartyScene extends BaseScene {
    private final SparseArray<IEntity> gridOrders = new SparseArray<IEntity>();
    private final List<Float> gridYList = new ArrayList<Float>();
    private final List<Rectangle> gridCollisionList = new ArrayList<Rectangle>();
    private final PartyInfo partyInfo = GameUserSession.getInstance().getPartyInfo();
    private final Party[] parties = partyInfo.getParties();
    private final float topbarY = cameraHeight - TextureEnum.PARTY_TOPBAR.getHeight();
    private final float frameY = topbarY - TextureEnum.PARTY_FRAME.getHeight();
    private final float frameTop = frameY + TextureEnum.PARTY_FRAME.getHeight();
    private final float gridHeight = TextureEnum.PARTY_FRAME_GRIDS.getHeight();
    private final Font mFont;
    private final Text hpText;
    private final Text atkText;

    private final Text[] partyHps = new Text[3];;
    private final Text[] partyAtks = new Text[3];

    public PartyScene(final GameActivity activity) throws IOException {
        super(activity);
        this.mFont = ResourceManager.getInstance().newFont(FontEnum.Main);
        hpText = new Text(this.simulatedLeftX + 360, topbarY + 48, mFont, "0123456789", vbom);
        atkText = new Text(this.simulatedLeftX + 600, topbarY + 48, mFont, "0123456789", vbom);
        final float partyTextTop = frameTop - 82;
        final float partyTextGap = gridHeight + 15;
        for (int partyIndex = 0; partyIndex < 3; partyIndex++) {

            final Text partyHp = new Text(this.simulatedLeftX + 110, partyTextTop - partyTextGap * partyIndex, mFont, "0123456789", vbom);
            final Text partyAtk = new Text(this.simulatedLeftX + 110, partyTextTop - 43 - partyTextGap * partyIndex, mFont, "0123456789", vbom);
            partyHps[partyIndex] = partyHp;
            partyAtks[partyIndex] = partyAtk;
        }

        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topbarSprite = createALBImageSprite(TextureEnum.PARTY_TOPBAR, this.simulatedLeftX, topbarY);
        this.attachChild(topbarSprite);
        this.attachChild(hpText);
        this.attachChild(atkText);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite frameSprite = createALBImageSprite(TextureEnum.PARTY_FRAME, this.simulatedLeftX, frameY);
        this.attachChild(frameSprite);

        for (int partyIndex = 0; partyIndex < 3; partyIndex++) {
            this.attachChild(partyHps[partyIndex]);
            this.attachChild(partyAtks[partyIndex]);
        }

        final F2ButtonSprite editSprite = createALBF2ButtonSprite(TextureEnum.PARTY_EDIT_BUTTON, TextureEnum.PARTY_EDIT_BUTTON_PRESSED,
                this.simulatedRightX - 135, 390);
        editSprite.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.PartyEdit, new IRCallback<BaseScene>() {

                    @Override
                    public BaseScene onCallback() {
                        try {
                            return new PartyEditScene(activity, 1);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                });

            }
        });
        this.attachChild(editSprite);
        this.registerTouchArea(editSprite);

        final F2ButtonSprite enhanceButton = createEnhanceButton();
        this.attachChild(enhanceButton);
        this.registerTouchArea(enhanceButton);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final boolean isSaveOk = CardUtils.saveParties();
                if (isSaveOk) {
                    ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
                } else {
                    alert("队伍保存失败！");
                }
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private F2ButtonSprite createEnhanceButton() {
        final F2ButtonSprite enhanceButton = createALBF2ButtonSprite(TextureEnum.PARTY_ENHANCE_BUTTON, TextureEnum.PARTY_ENHANCE_BUTTON_PRESSED,
                this.simulatedRightX - 135, 220);
        enhanceButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.CardUpgrade);
            }
        });
        return enhanceButton;
    }

    private Rectangle createGridCollisionArea(final IEntity sprite) {
        final Rectangle area = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight() * 0.65f, vbom);
        return area;
    }

    private IEntity createGridsTouchArea(final TextureEnum textureEnum, final float x, final float y) {
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final IEntity grid = new Rectangle(pX, pY, width, height, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                this.setZIndex(100);
                PartyScene.this.sortChildren();
                final float touchY = pSceneTouchEvent.getY();
                if (pSceneTouchEvent.isActionMove()) {
                    this.setPosition(pX, touchY);
                    final int thisOrder = gridOrders.indexOfValue(this);
                    for (int collisionIndex = 0; collisionIndex < gridCollisionList.size(); collisionIndex++) {
                        if (thisOrder == collisionIndex) {
                            continue;
                        }
                        final Rectangle collissionGrid = gridCollisionList.get(collisionIndex);
                        if (EntityCollisionChecker.checkContains(collissionGrid, pX, touchY)) {
                            final IEntity collissionSprite = gridOrders.get(collisionIndex);
                            gridOrders.put(thisOrder, collissionSprite);
                            gridOrders.put(collisionIndex, this);
                            collissionSprite.setPosition(pX, gridYList.get(thisOrder));
                            final Party tempParty = parties[thisOrder];
                            parties[thisOrder] = parties[collisionIndex];
                            parties[collisionIndex] = tempParty;

                            for (int partyIndex = 0; partyIndex < parties.length; partyIndex++) {
                                final Party party = parties[partyIndex];
                                partyHps[partyIndex].setText(String.valueOf(party.getHp()));
                                partyAtks[partyIndex].setText(String.valueOf(party.getAtk()));
                            }

                        }
                    }
                }
                if (pSceneTouchEvent.isActionUp() || pSceneTouchEvent.isActionOutside() || pSceneTouchEvent.isActionCancel()) {
                    this.setPosition(pX, gridYList.get(gridOrders.indexOfValue(this)));
                    this.setZIndex(IEntity.ZINDEX_DEFAULT);
                    PartyScene.this.sortChildren();
                }

                return true;
            }
        };
        grid.setAlpha(0);
        return grid;
    }

    private Sprite createGridSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final Sprite sprite = new Sprite(x, y, width, height, texture, vbom);
        return sprite;
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        hpText.setText(String.valueOf(partyInfo.getHp()));
        atkText.setText(String.valueOf(partyInfo.getAtk()));

        final int cardWidth = 135;
        final int cardHeight = 135;
        final float cardY = gridHeight * 0.5f;
        final int gridGap = 15;
        final int gap = 18;
        gridOrders.clear();
        gridYList.clear();
        gridCollisionList.clear();
        for (int partyIndex = 0; partyIndex < parties.length; partyIndex++) {
            final IEntity gridTouchArea = createGridsTouchArea(TextureEnum.PARTY_FRAME_GRIDS, 153, frameY + 337 - partyIndex * (gridHeight + gridGap));
            final Sprite gridSprite = createGridSprite(TextureEnum.PARTY_FRAME_GRIDS, gridTouchArea.getWidth() * 0.5f, gridTouchArea.getHeight() * 0.5f);
            gridTouchArea.setTag(888 + partyIndex);
            gridOrders.put(partyIndex, gridTouchArea);
            gridYList.add(gridTouchArea.getY());
            gridCollisionList.add(this.createGridCollisionArea(gridTouchArea));
            final IEntity oldGridSprite = this.getChildByTag(888 + partyIndex);
            this.unregisterTouchArea(oldGridSprite);
            this.detachChild(oldGridSprite);
            this.attachChild(gridTouchArea);
            this.registerTouchArea(gridTouchArea);

            final Party party = parties[partyIndex];
            final Card[] cards = party.getCards();
            for (int cardIndex = 0; cardIndex < cards.length; cardIndex++) {
                final Card card = cards[cardIndex];
                if (card != null) {
                    final CardAvatar cardAvatar = new CardAvatar(83f + (gap + cardWidth) * cardIndex, cardY, cardWidth, cardHeight, card, activity);
                    gridTouchArea.attachChild(cardAvatar);
                }

            }
            partyHps[partyIndex].setText(String.valueOf(party.getHp()));
            partyAtks[partyIndex].setText(String.valueOf(party.getAtk()));
            gridTouchArea.attachChild(gridSprite);
        }

    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}
