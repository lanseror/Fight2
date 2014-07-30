package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.Toast;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.util.CardUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class PartyScene extends BaseScene {
    private final SparseArray<IEntity> gridOrders = new SparseArray<IEntity>();
    private final List<Float> gridYList = new ArrayList<Float>();
    private final List<Rectangle> gridCollisionList = new ArrayList<Rectangle>();
    private final Card[][] cardParties = GameUserSession.getInstance().getParties();
    private final float topbarY = cameraHeight - TextureEnum.PARTY_TOPBAR.getHeight();
    private final float frameY = topbarY - TextureEnum.PARTY_FRAME.getHeight();

    public PartyScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topbarSprite = createALBImageSprite(TextureEnum.PARTY_TOPBAR, this.simulatedLeftX, topbarY);
        this.attachChild(topbarSprite);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth(), topbarY);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite frameSprite = createALBImageSprite(TextureEnum.PARTY_FRAME, this.simulatedLeftX, frameY);
        this.attachChild(frameSprite);

        updateScene();

        final F2ButtonSprite editSprite = createALBF2ButtonSprite(TextureEnum.PARTY_EDIT_BUTTON, TextureEnum.PARTY_EDIT_BUTTON_PRESSED,
                this.simulatedRightX - 140, 220);
        editSprite.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                try {
                    final Scene editScene = new PartyEditScene(activity, 1);
                    activity.getEngine().setScene(editScene);
                } catch (final IOException e) {
                    Debug.e(e);
                }
            }
        });
        this.attachChild(editSprite);
        this.registerTouchArea(editSprite);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 140, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final boolean isSaveOk = CardUtils.saveParties();
                if (isSaveOk) {
                    ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "队伍保存失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private Rectangle createGridCollisionArea(final IEntity sprite) {
        final Rectangle area = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight() * 0.65f, vbom);
        return area;
    }

    private IEntity createGridEntity(final TextureEnum textureEnum, final float x, final float y) {
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
        grid.setAlpha(0);
        return grid;
    }

    private Sprite createGridSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final Sprite sprite = new Sprite(x, y, width, height, texture, vbom);
        return sprite;
    }

    @Override
    public void updateScene() {
        final float gridHeight = TextureEnum.PARTY_FRAME_GRID.getHeight();
        final int cardWidth = 135;
        final int cardHeight = 135;
        final float cardY = gridHeight * 0.5f;
        final int gridGap = 15;
        final int gap = 18;
        gridOrders.clear();
        gridYList.clear();
        gridCollisionList.clear();
        for (int partyIndex = 0; partyIndex < cardParties.length; partyIndex++) {
            final IEntity gridEntity = createGridEntity(TextureEnum.PARTY_FRAME_GRID, 153, frameY + 337 - partyIndex * (gridHeight + gridGap));
            final Sprite gridSprite = createGridSprite(TextureEnum.PARTY_FRAME_GRID, gridEntity.getWidth() * 0.5f, gridEntity.getHeight() * 0.5f);
            gridEntity.setTag(888 + partyIndex);
            gridOrders.put(partyIndex, gridEntity);
            gridYList.add(gridEntity.getY());
            gridCollisionList.add(this.createGridCollisionArea(gridEntity));
            final IEntity oldGridSprite = this.getChildByTag(888 + partyIndex);
            this.unregisterTouchArea(oldGridSprite);
            this.detachChild(oldGridSprite);
            this.attachChild(gridEntity);
            this.registerTouchArea(gridEntity);

            final Card[] cards = cardParties[partyIndex];
            for (int cardIndex = 0; cardIndex < cards.length; cardIndex++) {
                final Card card = cards[cardIndex];
                if (card != null) {
                    final ITextureRegion cardTextureRegion = TextureFactory.getInstance().getIextureRegion(card.getAvatar());
                    final Sprite cardSprite = new Sprite(83f + (gap + cardWidth) * cardIndex, cardY, cardWidth, cardHeight, cardTextureRegion, vbom);
                    gridEntity.attachChild(cardSprite);
                }

            }

            gridEntity.attachChild(gridSprite);
        }

    }

}
