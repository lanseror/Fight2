package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.ArenaReward;
import com.fight2.entity.ArenaReward.ArenaRewardType;
import com.fight2.entity.ArenaRewardItem;
import com.fight2.entity.ArenaRewardItem.ArenaRewardItemType;
import com.fight2.entity.Card;
import com.fight2.entity.UserArenaInfo;
import com.fight2.util.ArenaUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class ArenaRewardScene extends BaseScene implements IScrollDetectorListener {
    private final static float CLIP_HEIGHT = 460;
    private final static float CONTAINER_INIT_Y = CLIP_HEIGHT * 0.5f;
    private final float frameWidth = TextureEnum.ARENA_REWARD_BG.getWidth();
    private final float frameHeight = TextureEnum.ARENA_REWARD_BG.getHeight();
    private final float frameCenter = frameWidth * 0.5f;
    private final float frameY = simulatedHeight - frameHeight * 0.5f;
    private final SurfaceScrollDetector scrollDetector;
    private final IEntity mightContainer;
    private final IEntity rankContainer;
    private float scrollMightBottomY = 0;
    private float scrollRankBottomY = 0;
    private final Font descTitleFont;
    private final Font descFont;
    private final Font mightFont;
    private final Font rankFont;
    private final Font amountFont;
    private final Font itemFont;
    private final Text mightDescTitleText;
    private final Text rankDescTitleText;
    private final Text mightDescText;
    private final Text rankDescText;
    private static String MIGHT_DESC_TITLE = "已领取奖励: %s/%s";
    private static String RANK_DESC_TITLE = "你的排名：%s";
    private static String MIGHT_DESC = "在战斗中胜利可获得力量，从而赢得奖励。以下列表显示了你完成每个阶段时可以获得的奖励。达到力量要求后，系统会将奖励发送到你的宝库中。";
    private static String RANK_DESC = "只有最强大的英雄才配获得最丰厚的奖励！以下列表显示了你名列前茅时能够获得的奖励。完成赛季的战斗后，就会根据排名获得奖励。";
    private final UserArenaInfo userArenaInfo;

    public ArenaRewardScene(final GameActivity activity, final UserArenaInfo userArenaInfo) throws IOException {
        super(activity);
        this.userArenaInfo = userArenaInfo;
        this.descTitleFont = ResourceManager.getInstance().newFont(FontEnum.Bold, 30);
        this.descFont = ResourceManager.getInstance().newFont(FontEnum.Default, 18);
        this.mightFont = ResourceManager.getInstance().newFont(FontEnum.Bold, 26);
        this.rankFont = ResourceManager.getInstance().newFont(FontEnum.Bold, 30);
        this.amountFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        this.itemFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        this.mightDescTitleText = new Text(291, 145, descTitleFont, MIGHT_DESC_TITLE, 15, vbom);
        mightDescTitleText.setColor(0XFF683905);
        this.rankDescTitleText = new Text(291, 145, descTitleFont, String.format(RANK_DESC_TITLE, userArenaInfo.getRankNumber()), vbom);
        rankDescTitleText.setColor(0XFF683905);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 485);
        this.mightDescText = new Text(291, 80, descFont, MIGHT_DESC, textOptions, vbom);
        mightDescText.setColor(0XFF683905);
        this.rankDescText = new Text(291, 80, descFont, RANK_DESC, textOptions, vbom);
        rankDescText.setColor(0XFF683905);
        scrollDetector = new SurfaceScrollDetector(this);
        mightContainer = new Rectangle(frameCenter, CONTAINER_INIT_Y, frameWidth, CLIP_HEIGHT, vbom);
        mightContainer.setAlpha(0);
        rankContainer = new Rectangle(frameCenter, CONTAINER_INIT_Y, frameWidth, CLIP_HEIGHT, vbom);
        rankContainer.setAlpha(0);
        rankContainer.setVisible(false);
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.5f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final Sprite frameSprite = createACImageSprite(TextureEnum.ARENA_REWARD_BG, cameraCenterX, frameY);
        this.attachChild(frameSprite);

        final IEntity closeTouchArea = new Rectangle(frameWidth - 10, frameHeight - 15, 130, 120, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    back();
                    activity.getGameHub().needSmallChatRoom(true);
                    return true;
                }
                return false;

            }
        };
        closeTouchArea.setAlpha(0);
        frameSprite.attachChild(closeTouchArea);
        this.registerTouchArea(closeTouchArea);

        final TextureEnum descEnum = TextureEnum.ARENA_REWARD_DESC;
        // Might elements
        final TextureEnum mightButtonEnum = TextureEnum.ARENA_REWARD_MIGHT_BUTTON;
        final IEntity mightButton = createACImageSprite(mightButtonEnum, frameCenter, CLIP_HEIGHT - mightButtonEnum.getHeight() * 0.5f);
        mightContainer.attachChild(mightButton);
        final IEntity mightTouchArea = createMightTouchArea();
        mightButton.attachChild(mightTouchArea);
        this.registerTouchArea(mightTouchArea);

        final Sprite mightDesc = createACImageSprite(descEnum, frameCenter, CLIP_HEIGHT - mightButton.getHeight() - descEnum.getHeight() * 0.5f);
        mightDesc.attachChild(mightDescTitleText);
        mightDesc.attachChild(mightDescText);
        mightContainer.attachChild(mightDesc);

        // Rank elements
        final TextureEnum rankButtonEnum = TextureEnum.ARENA_REWARD_RANK_BUTTON;
        final IEntity rankButton = createACImageSprite(rankButtonEnum, frameCenter, CLIP_HEIGHT - rankButtonEnum.getHeight() * 0.5f);
        rankContainer.attachChild(rankButton);
        final IEntity rankTouchArea = createRankTouchArea();
        rankButton.attachChild(rankTouchArea);
        this.registerTouchArea(rankTouchArea);
        final Sprite rankDesc = createACImageSprite(descEnum, frameCenter, CLIP_HEIGHT - rankButton.getHeight() - descEnum.getHeight() * 0.5f);
        rankDesc.attachChild(rankDescTitleText);
        rankDesc.attachChild(rankDescText);
        rankContainer.attachChild(rankDesc);

        final ClipEntity rewardListTouchArea = new ClipEntity(frameCenter, 250, frameWidth, CLIP_HEIGHT) {
            @Override
            public boolean onAreaTouched(final TouchEvent touchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (mightButton.contains(touchEvent.getX(), touchEvent.getY())) {
                    return false;
                } else {
                    scrollDetector.onTouchEvent(touchEvent);
                    return true;
                }
            }
        };
        frameSprite.attachChild(rewardListTouchArea);
        this.registerTouchArea(rewardListTouchArea);
        rewardListTouchArea.attachChild(mightContainer);
        rewardListTouchArea.attachChild(rankContainer);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private IEntity createRewardGrid(final float x, final float y) {
        final TextureEnum textureEnum = TextureEnum.ARENA_REWARD_GRID;
        final Sprite rewardGrid = createACImageSprite(textureEnum, x, y);
        return rewardGrid;

    }

    private IEntity createMightTouchArea() {
        final IEntity touchArea = new Rectangle(140, 30, 250, 60, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    mightContainer.setVisible(true);
                    rankContainer.setVisible(false);
                    return true;
                }
                return false;

            }
        };
        touchArea.setAlpha(0);
        return touchArea;
    }

    private IEntity createRankTouchArea() {
        final IEntity touchArea = new Rectangle(405, 30, 230, 60, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    mightContainer.setVisible(false);
                    rankContainer.setVisible(true);
                    return true;
                }
                return false;

            }
        };
        touchArea.setAlpha(0);
        return touchArea;
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        final List<ArenaReward> arenaRewards = ArenaUtils.getArenaReward(activity);
        final List<ArenaReward> mightRewards = new ArrayList<ArenaReward>();
        final List<ArenaReward> rankRewards = new ArrayList<ArenaReward>();
        for (final ArenaReward arenaReward : arenaRewards) {
            if (arenaReward.getType() == ArenaRewardType.Might) {
                mightRewards.add(arenaReward);
            } else {
                rankRewards.add(arenaReward);
            }
        }

        final TextureEnum descEnum = TextureEnum.ARENA_REWARD_DESC;
        final TextureEnum gridEnum = TextureEnum.ARENA_REWARD_GRID;
        final float gridWidth = gridEnum.getWidth();
        final float gridHeight = gridEnum.getHeight();
        // Might elements
        final float gridInitY = CLIP_HEIGHT - TextureEnum.ARENA_REWARD_MIGHT_BUTTON.getHeight() - descEnum.getHeight() - gridHeight * 0.5f + 2;
        float mightGridBottomY = 0;
        for (int i = 0; i < mightRewards.size(); i++) {
            final ArenaReward mightReward = mightRewards.get(i);
            mightGridBottomY = gridInitY - gridHeight * i;
            final IEntity rewardGrid = createRewardGrid(frameCenter, mightGridBottomY);
            final TextureEnum mightPointEnum = TextureEnum.ARENA_REWARD_MIGHT_POINT;
            final IEntity mightPointImg = createALBImageSprite(mightPointEnum, 40, gridHeight - mightPointEnum.getHeight() - 11);
            final Text mightText = new Text(42, 25, mightFont, String.valueOf(mightReward.getMin()), vbom);
            mightPointImg.attachChild(mightText);
            rewardGrid.attachChild(mightPointImg);
            insertRewardItems(mightReward.getRewardItems(), rewardGrid);
            mightContainer.attachChild(rewardGrid);
        }
        scrollMightBottomY = CONTAINER_INIT_Y - mightGridBottomY + gridHeight * 0.5f;
        mightDescTitleText.setText(String.format(MIGHT_DESC_TITLE, userArenaInfo.getIssuedReward(), mightRewards.size()));

        // Rank elements
        float rankGridBottomY = 0;
        for (int i = 0; i < rankRewards.size(); i++) {
            final ArenaReward rankReward = rankRewards.get(i);
            rankGridBottomY = gridInitY - gridHeight * i;
            final IEntity rewardGrid = createRewardGrid(frameCenter, rankGridBottomY);
            final Text rankText = new Text(gridWidth * 0.5f, gridHeight - 55, rankFont, String.format("排名: %s-%s", rankReward.getMin(), rankReward.getMax()),
                    vbom);
            rankText.setColor(0XFFF9B552);
            rewardGrid.attachChild(rankText);
            insertRewardItems(rankReward.getRewardItems(), rewardGrid);
            rankContainer.attachChild(rewardGrid);
        }
        scrollRankBottomY = CONTAINER_INIT_Y - rankGridBottomY + gridHeight * 0.5f;

    }

    private void insertRewardItems(final List<ArenaRewardItem> rewardItems, final IEntity rewardGrid) {
        final TextureEnum itemGridEnum = TextureEnum.ARENA_REWARD_ITEM_GRID;
        final TextureEnum ticketEnum = TextureEnum.COMMON_ARENA_TICKET;
        final TextureEnum staminaEnum = TextureEnum.COMMON_STAMINA;
        final TextureEnum guildContributionEnum = TextureEnum.COMMON_GUILD_CONTRIBUTION;
        final float itemWidth = 194;
        final float itemGridInitX = itemWidth * 0.5f;
        final float itemGridY = 100;
        final float itemX = itemGridEnum.getWidth() * 0.5f;
        final float itemY = itemGridEnum.getHeight() * 0.5f;
        for (int itemIndex = 0; itemIndex < rewardItems.size(); itemIndex++) {
            final ArenaRewardItem rewardItem = rewardItems.get(itemIndex);
            final ArenaRewardItemType rewardItemType = rewardItem.getType();

            final Text amountText = new Text(95, 25, amountFont, String.format("×%s", rewardItem.getAmount()), vbom);
            amountText.setColor(0XFFAECE01);
            amountText.setX(itemGridEnum.getWidth() - amountText.getWidth() * 0.5f - 10);
            if (rewardItemType == ArenaRewardItemType.ArenaTicket) {
                final IEntity ticketGrid = createACImageSprite(itemGridEnum, itemGridInitX + itemWidth * itemIndex, itemGridY);
                final IEntity ticketImg = createACImageSprite(ticketEnum, itemX, itemY);
                final Text itemText = new Text(itemX, itemGridEnum.getHeight() + 25, itemFont, "竞技场门票", vbom);
                itemText.setColor(0XFFFFE8C6);
                ticketGrid.attachChild(itemText);
                ticketGrid.attachChild(ticketImg);
                ticketGrid.attachChild(amountText);
                rewardGrid.attachChild(ticketGrid);
            } else if (rewardItemType == ArenaRewardItemType.Stamina) {
                final IEntity staminaGrid = createACImageSprite(itemGridEnum, itemGridInitX + itemWidth * itemIndex, itemGridY);
                final IEntity staminaImg = createACImageSprite(staminaEnum, itemX - 10, itemY - 5);
                final Text itemText = new Text(itemX, itemGridEnum.getHeight() + 25, itemFont, "精力药水", vbom);
                itemText.setColor(0XFFFFE8C6);
                staminaGrid.attachChild(itemText);
                staminaGrid.attachChild(staminaImg);
                staminaGrid.attachChild(amountText);
                rewardGrid.attachChild(staminaGrid);
            } else if (rewardItemType == ArenaRewardItemType.GuildContribution) {
                final IEntity guildContributionGrid = createACImageSprite(itemGridEnum, itemGridInitX + itemWidth * itemIndex, itemGridY);
                final IEntity guildContributionImg = createACImageSprite(guildContributionEnum, itemX, itemY + 3);
                final Text itemText = new Text(itemX, itemGridEnum.getHeight() + 25, itemFont, "公会贡献值", vbom);
                itemText.setColor(0XFFFFE8C6);
                guildContributionGrid.attachChild(itemText);
                guildContributionGrid.attachChild(guildContributionImg);
                guildContributionGrid.attachChild(amountText);
                rewardGrid.attachChild(guildContributionGrid);
            } else if (rewardItemType == ArenaRewardItemType.Card) {
                final Card card = rewardItem.getCard();
                final ITextureRegion cardTexture = TextureFactory.getInstance().getTextureRegion(card.getImage());
                final float cardAdjustX = (itemIndex > 0 ? 15 : 0);
                final Sprite cardSprite = new Sprite(itemGridInitX + (itemWidth - cardAdjustX) * itemIndex, itemGridY + 15, 110, 165, cardTexture, vbom);
                amountText.setX(cardSprite.getWidth() + amountText.getWidth() * 0.5f + 5);
                cardSprite.attachChild(amountText);
                rewardGrid.attachChild(cardSprite);
            }

        }
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

    private void handleScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        final float mightToY = mightContainer.getY() - pDistanceY;
        final float rankToY = rankContainer.getY() - pDistanceY;
        if (mightToY < CONTAINER_INIT_Y) {
            mightContainer.setY(CONTAINER_INIT_Y);
        } else {
            if (mightContainer.isVisible()) {
                if (mightToY > scrollMightBottomY) {
                    mightContainer.setY(scrollMightBottomY);
                } else {
                    mightContainer.setY(mightToY);
                }
            } else {
                if (rankToY > scrollRankBottomY) {
                    mightContainer.setY(scrollRankBottomY);
                } else {
                    mightContainer.setY(rankToY);
                }
            }
        }

        if (rankToY < CONTAINER_INIT_Y) {
            rankContainer.setY(CONTAINER_INIT_Y);
        } else {
            if (rankContainer.isVisible()) {
                if (rankToY > scrollRankBottomY) {
                    rankContainer.setY(scrollRankBottomY);
                } else {
                    rankContainer.setY(rankToY);
                }
            } else {
                if (mightToY > scrollMightBottomY) {
                    rankContainer.setY(scrollMightBottomY);
                } else {
                    rankContainer.setY(mightToY);
                }
            }
        }
    }

    @Override
    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        handleScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
    }

    @Override
    public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        handleScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
    }

    @Override
    public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        handleScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
    }

}
