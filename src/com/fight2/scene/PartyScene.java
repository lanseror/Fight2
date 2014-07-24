package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.algorithm.collision.EntityCollisionChecker;
import org.andengine.util.debug.Debug;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.util.TextureFactory;

public class PartyScene extends BaseScene {
    private final SparseArray<Sprite> gridOrders = new SparseArray<Sprite>();
    private final List<Float> gridYList = new ArrayList<Float>();
    private final List<Rectangle> gridCollisionList = new ArrayList<Rectangle>();
    private final Card[][] cardParties = GameUserSession.getInstance().getParties();
    final Map<SceneEnum, BaseScene> scenes = this.activity.getScenes();

    public PartyScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final float frameY = cameraHeight - TextureEnum.PARTY_FRAME.getHeight();
        final Sprite frameSprite = createRealScreenImageSprite(TextureEnum.PARTY_FRAME, 0, frameY);
        this.attachChild(frameSprite);

        updateScene();

        final Sprite editSprite = createEditSprite(TextureEnum.PARTY_EDIT_BUTTON, 713, frameY + 340, 1);
        this.attachChild(editSprite);
        this.registerTouchArea(editSprite);
        final Sprite editSprite2 = createEditSprite(TextureEnum.PARTY_EDIT_BUTTON, 713, frameY + 197, 2);
        this.attachChild(editSprite2);
        this.registerTouchArea(editSprite2);
        final Sprite editSprite3 = createEditSprite(TextureEnum.PARTY_EDIT_BUTTON, 713, frameY + 53, 3);
        this.attachChild(editSprite3);
        this.registerTouchArea(editSprite3);

        final F2ButtonSprite backButton = createRealScreenF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, this.simulatedWidth - 100, 20);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                activity.getEngine().setScene(scenes.get(SceneEnum.Main));
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private Rectangle createGridCollisionArea(final Sprite sprite) {
        final Rectangle area = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight() * 0.65f, vbom);
        return area;
    }

    private Sprite createEditSprite(final TextureEnum textureEnum, final float x, final float y, final int partyNumber) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    try {
                        final Scene editScene = new PartyEditScene(activity, partyNumber);
                        activity.getEngine().setScene(editScene);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        };
        return sprite;
    }

    private Sprite createGridSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom) {
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
                            final Sprite collissionSprite = gridOrders.get(collisionIndex);
                            gridOrders.put(thisOrder, collissionSprite);
                            gridOrders.put(collisionIndex, this);
                            collissionSprite.setPosition(pX, gridYList.get(thisOrder));
                            final Card[] tempParty = cardParties[thisOrder];
                            cardParties[thisOrder] = cardParties[collisionIndex];
                            cardParties[collisionIndex] = tempParty;
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
        return sprite;
    }

    @Override
    public void updateScene() {
        final float frameY = cameraHeight - TextureEnum.PARTY_FRAME.getHeight();
        final int cardWidth = 94;
        final int cardHeight = 94;
        final float cardY = 47f;
        final int gap = 37;
        gridOrders.clear();
        gridYList.clear();
        gridCollisionList.clear();
        for (int partyIndex = 0; partyIndex < cardParties.length; partyIndex++) {
            final Sprite gridSprite = createGridSprite(TextureEnum.PARTY_FRAME_GRID, 196, frameY + 323 - partyIndex * 144);
            gridSprite.setTag(888 + partyIndex);
            gridOrders.put(partyIndex, gridSprite);
            gridYList.add(gridSprite.getY());
            gridCollisionList.add(this.createGridCollisionArea(gridSprite));
            final IEntity oldGridSprite = this.getChildByTag(888 + partyIndex);
            this.unregisterTouchArea(oldGridSprite);
            this.detachChild(oldGridSprite);
            this.attachChild(gridSprite);
            this.registerTouchArea(gridSprite);

            final Card[] cards = cardParties[partyIndex];
            for (int cardIndex = 0; cardIndex < cards.length; cardIndex++) {
                final Card card = cards[cardIndex];
                if (card != null) {
                    try {
                        final ITextureRegion cardTextureRegion = createCardTexture(card.getImage());
                        final Sprite cardSprite = new Sprite(49f + (gap + cardWidth) * cardIndex, cardY, cardWidth, cardHeight, cardTextureRegion, vbom);
                        gridSprite.attachChild(cardSprite);
                    } catch (final IOException e) {
                        Debug.e(e);
                    }
                }

            }

        }

    }

}
