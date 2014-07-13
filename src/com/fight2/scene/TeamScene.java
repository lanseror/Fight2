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
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.algorithm.collision.EntityCollisionChecker;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
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

    private final SparseArray<Sprite> gridOrders = new SparseArray<Sprite>();
    private final List<Float> gridYList = new ArrayList<Float>();
    private final List<Rectangle> gridCollisionList = new ArrayList<Rectangle>();
    private final List<List<Card>> cardParties = GameUserSession.getInstance().getParties();

    public TeamScene(final GameActivity activity) throws IOException {
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

    private void init() throws IOException {
        final Sprite bgSprite = createImageSprite2(TextureEnum.TEAM_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final float frameY = cameraHeight - TextureEnum.TEAM_FRAME.getHeight();
        final Sprite frameSprite = createImageSprite(TextureEnum.TEAM_FRAME, 0, frameY);
        this.attachChild(frameSprite);

        final int cardWidth = 94;
        final int cardHeight = 94;
        final float cardY = 47f;
        final int gap = 37;

        for (int partyIndex = 0; partyIndex < cardParties.size(); partyIndex++) {
            final Sprite gridSprite = createGridSprite(TextureEnum.TEAM_FRAME_GRID, 196, frameY + 323 - partyIndex * 144);
            gridOrders.put(partyIndex, gridSprite);
            gridYList.add(gridSprite.getY());
            gridCollisionList.add(this.createGridCollisionArea(gridSprite));
            this.attachChild(gridSprite);
            this.registerTouchArea(gridSprite);

            final List<Card> cards = cardParties.get(partyIndex);
            for (int cardIndex = 0; cardIndex < cards.size(); cardIndex++) {
                final Card card = cards.get(cardIndex);
                if (card != null) {
                    final ITextureRegion cardTextureRegion = createCardTexture(card.getImage());
                    final Sprite cardSprite = new Sprite(49f + (gap + cardWidth) * cardIndex, cardY, cardWidth, cardHeight, cardTextureRegion, vbom);
                    gridSprite.attachChild(cardSprite);
                }

            }

        }

        final Sprite organizeSprite = createImageSprite(TextureEnum.TEAM_ORGANIZE_BUTTON, 713, frameY + 340);
        this.attachChild(organizeSprite);
        final Sprite organizeSprite2 = createImageSprite(TextureEnum.TEAM_ORGANIZE_BUTTON, 713, frameY + 197);
        this.attachChild(organizeSprite2);
        final Sprite organizeSprite3 = createImageSprite(TextureEnum.TEAM_ORGANIZE_BUTTON, 713, frameY + 53);
        this.attachChild(organizeSprite3);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private ITextureRegion createCardTexture(final String imageUrl) throws IOException {
        final ITexture texture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), imageUrl);
        final ITextureRegion textureRegion = TextureRegionFactory.extractFromTexture(texture);
        texture.load();
        return textureRegion;
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
        final Sprite sprite = new Sprite(cameraCenterX, cameraCenterY, width, height, texture, vbom);
        return sprite;
    }
}
