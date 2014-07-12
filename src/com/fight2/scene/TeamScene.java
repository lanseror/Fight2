package com.fight2.scene;

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
import org.andengine.entity.sprite.batch.SpriteGroup;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.algorithm.collision.EntityCollisionChecker;
import org.andengine.util.debug.Debug;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.util.ConfigHelper;
import com.fight2.util.TextureFactory;

public class TeamScene extends Scene {
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final float cameraCenterX;
    private final float cameraCenterY;
    private final int cameraWidth;
    private final int cameraHeight;
    private final int deviceWidth;
    private final int deviceHeight;

    private final String[] actions = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE" };

    private final SparseArray<Sprite> gridOrders = new SparseArray<Sprite>();
    private final List<Float> gridYList = new ArrayList<Float>();
    private final List<Rectangle> gridCollisionList = new ArrayList<Rectangle>();

    public TeamScene(final GameActivity activity) {
        super();
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        final ConfigHelper configHelper = ConfigHelper.getInstance();
        this.cameraCenterX = configHelper.getFloat(ConfigEnum.CameraCenterX);
        this.cameraCenterY = configHelper.getFloat(ConfigEnum.CameraCenterY);
        this.cameraWidth = configHelper.getInt(ConfigEnum.CameraWidth);
        this.cameraHeight = configHelper.getInt(ConfigEnum.CameraHeight);
        this.deviceWidth = configHelper.getInt(ConfigEnum.DeviceWidth);
        this.deviceHeight = configHelper.getInt(ConfigEnum.DeviceHeight);
        init();
    }

    private void init() {
        final Sprite bgSprite = createImageSprite2(TextureEnum.TEAM_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final float frameY = cameraHeight - TextureEnum.TEAM_FRAME.getHeight();
        final Sprite frameSprite = createImageSprite(TextureEnum.TEAM_FRAME, 0, frameY);
        this.attachChild(frameSprite);

        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion card1Texture = textureFactory.getIextureRegion(TextureEnum.TEST_CARD1);
        final int cardWidth = 94;
        final int cardHeight = 94;
        final float cardY = 47f;
        final int gap = 37;
        final Sprite card1 = new Sprite(49f, cardY, cardWidth, cardHeight, card1Texture, vbom);
        final Sprite card11 = new Sprite(card1.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card1Texture, vbom);
        final Sprite card111 = new Sprite(card11.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card1Texture, vbom);
        final Sprite card1111 = new Sprite(card111.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card1Texture, vbom);

        final Sprite gridSprite1 = createGridSprite(TextureEnum.TEAM_FRAME_GRID, 196, frameY + 322);
        gridOrders.put(0, gridSprite1);
        gridYList.add(gridSprite1.getY());
        gridCollisionList.add(this.createGridCollisionArea(gridSprite1));
        this.attachChild(gridSprite1);
        this.registerTouchArea(gridSprite1);
        gridSprite1.attachChild(card1);
        gridSprite1.attachChild(card11);
        gridSprite1.attachChild(card111);
        gridSprite1.attachChild(card1111);

        final ITextureRegion card2Texture = textureFactory.getIextureRegion(TextureEnum.TEST_CARD2);
        final Sprite card2 = new Sprite(49f, cardY, cardWidth, cardHeight, card2Texture, vbom);
        final Sprite card22 = new Sprite(card2.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card2Texture, vbom);
        final Sprite card222 = new Sprite(card22.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card2Texture, vbom);
        final Sprite card2222 = new Sprite(card222.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card2Texture, vbom);

        final Sprite gridSprite2 = createGridSprite(TextureEnum.TEAM_FRAME_GRID, 196, frameY + 179);
        gridOrders.put(1, gridSprite2);
        gridYList.add(gridSprite2.getY());
        gridCollisionList.add(this.createGridCollisionArea(gridSprite2));
        this.attachChild(gridSprite2);
        this.registerTouchArea(gridSprite2);
        gridSprite2.attachChild(card2);
        gridSprite2.attachChild(card22);
        gridSprite2.attachChild(card222);
        gridSprite2.attachChild(card2222);

        final ITextureRegion card3Texture = textureFactory.getIextureRegion(TextureEnum.TEST_CARD3);
        final Sprite card3 = new Sprite(49f, cardY, cardWidth, cardHeight, card3Texture, vbom);
        final Sprite card33 = new Sprite(card3.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card3Texture, vbom);
        final Sprite card333 = new Sprite(card33.getX() + gap + cardWidth, cardY, cardWidth, cardHeight, card3Texture, vbom);
        final Sprite gridSprite3 = createGridSprite(TextureEnum.TEAM_FRAME_GRID, 196, frameY + 35);
        gridOrders.put(2, gridSprite3);
        gridYList.add(gridSprite3.getY());
        gridCollisionList.add(this.createGridCollisionArea(gridSprite3));
        this.attachChild(gridSprite3);
        this.registerTouchArea(gridSprite3);
        gridSprite3.attachChild(card3);
        gridSprite3.attachChild(card33);
        gridSprite3.attachChild(card333);

        final Sprite organizeSprite = createImageSprite(TextureEnum.TEAM_BUTTON_ORGANIZE, 713, frameY + 340);
        this.attachChild(organizeSprite);
        final Sprite organizeSprite2 = createImageSprite(TextureEnum.TEAM_BUTTON_ORGANIZE, 713, frameY + 197);
        this.attachChild(organizeSprite2);
        final Sprite organizeSprite3 = createImageSprite(TextureEnum.TEAM_BUTTON_ORGANIZE, 713, frameY + 53);
        this.attachChild(organizeSprite3);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        Debug.e("isTouchAreaBindingOnActionDownEnabled:  " + this.isTouchAreaBindingOnActionDownEnabled());
        Debug.e("setTouchAreaBindingOnActionMoveEnabled:  " + this.isTouchAreaBindingOnActionMoveEnabled());
    }

    private Rectangle createGridCollisionArea(final Sprite sprite) {
        final Rectangle area = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight() * 0.65f, vbom);
        return area;
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
                TeamScene.this.sortChildren();
                final float touchY = pSceneTouchEvent.getY();
                if (pSceneTouchEvent.isActionMove()) {
                    this.setPosition(pX, touchY);
                    final int thisOrder = gridOrders.indexOfValue(this);
                    for (int i = 0; i < gridCollisionList.size(); i++) {
                        if (thisOrder == i) {
                            continue;
                        }
                        final Rectangle collissionGrid = gridCollisionList.get(i);
                        if (EntityCollisionChecker.checkContains(collissionGrid, pX, touchY)) {
                            final Sprite collissionSprite = gridOrders.get(i);
                            gridOrders.put(thisOrder, collissionSprite);
                            gridOrders.put(i, this);
                            collissionSprite.setPosition(pX, gridYList.get(thisOrder));
                        }
                    }
                }
                if (pSceneTouchEvent.isActionUp() || pSceneTouchEvent.isActionOutside() || pSceneTouchEvent.isActionCancel()) {
                    this.setPosition(pX, gridYList.get(gridOrders.indexOfValue(this)));
                    this.setZIndex(IEntity.ZINDEX_DEFAULT);
                    TeamScene.this.sortChildren();
                }

                return true;
            }
        };
        return sprite;
    }

    private Sprite createImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom);
        return sprite;
    }

    private Sprite createImageSprite2(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(cameraCenterX, cameraCenterY, width, height, texture, vbom);
        return sprite;
    }
}
