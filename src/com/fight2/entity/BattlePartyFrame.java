package com.fight2.entity;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.TextureEnum;
import com.fight2.util.StringUtils;
import com.fight2.util.TextureFactory;

public class BattlePartyFrame extends Rectangle {
    public final static int WIDTH = 312;
    public final static int HEIGHT = 165;
    public final static int CARD_WIDTH = 100;
    public final static int CARD_HEIGHT = 150;
    public final static int AVATAR_WIDTH = 72;
    public final static int AVATAR_HEIGHT = 72;
    private final float initX;
    private final float initY;
    private final VertexBufferObjectManager vbom;
    private HpBar hpBar;

    public BattlePartyFrame(final float pX, final float pY, final Party party, final VertexBufferObjectManager pVertexBufferObjectManager,
            final boolean isBottom) {
        super(pX + WIDTH * 0.5f, pY + HEIGHT * 0.5f, WIDTH, HEIGHT, pVertexBufferObjectManager);
        super.setAlpha(0);
        this.vbom = pVertexBufferObjectManager;
        if (isBottom) {
            this.createBottomCard(party);
            this.createBottomFrame(party);
        } else {
            final float height = 81 + AVATAR_HEIGHT - 5;
            this.setHeight(height);
            this.setY(pY + height * 0.5f);
            this.createTopCard(party);
            this.createTopFrame(party);
        }
        this.initX = this.getX();
        this.initY = this.getY();

    }

    private void createBottomCard(final Party party) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final float startX = 60;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }

            final ITextureRegion texture = textureFactory.getTextureRegion(card.getImage());
            final Sprite cardSprite = new Sprite(startX + 65 * i, 90, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
            this.attachChild(cardSprite);
        }
    }

    private void createTopCard(final Party party) {
        final TextureFactory textureFactory = TextureFactory.getInstance();

        final float avatarGap = 1;
        final float startX = 10 + AVATAR_WIDTH * 0.5f;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }
            final String avatar = card.getAvatar();
            final ITextureRegion texture = StringUtils.isEmpty(avatar) ? textureFactory.getAssetTextureRegion(TextureEnum.COMMON_DEFAULT_AVATAR)
                    : textureFactory.getTextureRegion(avatar);
            final Sprite cardSprite = new Sprite(startX + (AVATAR_WIDTH + avatarGap) * i, AVATAR_HEIGHT * 0.5f, AVATAR_WIDTH, AVATAR_HEIGHT, texture, vbom);
            this.attachChild(cardSprite);
        }

    }

    private void createTopFrame(final Party party) {
        final Sprite battlePartyFrame = createPartyFrame(TextureEnum.BATTLE_PARTY_TOP, 0, AVATAR_HEIGHT - 5, false);
        // hpBar = new HpBar(156, 22, vbom, party.getHp(), false);
        // battlePartyFrame.attachChild(hpBar);
        this.attachChild(battlePartyFrame);
    }

    private void createBottomFrame(final Party party) {
        final Sprite battlePartyFrame = createPartyFrame(TextureEnum.BATTLE_PARTY_BOTTOM, 0, 0, true);
        // hpBar = new HpBar(158, 61, vbom, party.getHp(), true);
        // battlePartyFrame.attachChild(hpBar);
        this.attachChild(battlePartyFrame);
    }

    private Sprite createPartyFrame(final TextureEnum textureEnum, final float x, final float y, final boolean isBottom) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite partyFrame = new Sprite(pX, pY, texture, vbom);
        return partyFrame;
    }

    public HpBar getHpBar() {
        return hpBar;
    }

    public void setHpBar(final HpBar hpBar) {
        this.hpBar = hpBar;
    }

    public float getInitX() {
        return initX;
    }

    public float getInitY() {
        return initY;
    }

}
