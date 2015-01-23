package com.fight2.scene;

import java.io.IOException;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.QuestTask;
import com.fight2.entity.QuestTask.UserTaskStatus;
import com.fight2.entity.Reward;
import com.fight2.entity.RewardItem;
import com.fight2.entity.RewardItem.RewardItemType;
import com.fight2.entity.engine.CardFrame;
import com.fight2.entity.engine.CommonDialogFrame;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.TextDialogFrame;
import com.fight2.util.ICallback;
import com.fight2.util.ResourceManager;
import com.fight2.util.TaskUtils;

public class TaskGuideScene extends BaseScene {
    private final ICallback iLeaveCallback;

    public TaskGuideScene(final GameActivity activity, final ICallback iLeaveCallback) throws IOException {
        super(activity);
        this.iLeaveCallback = iLeaveCallback;
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.3f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final Sprite msgSprite = this.createALBImageSprite(TextureEnum.MAIN_MSG, 45, 0);
        this.attachChild(msgSprite);
        final QuestTask task = TaskUtils.getTask();
        if (task.getStatus() == UserTaskStatus.Ready) {
            createDialogFrame(task);
        } else if (task.getStatus() == UserTaskStatus.Started) {
            createTipsFrame(task);
        } else if (task.getStatus() == UserTaskStatus.Finished) {
            createRewardFrame(task);
        } else if (task.getStatus() == UserTaskStatus.End) {
            createEmptyFrame(task);
        }

    }

    @Override
    public void updateScene() {
    }

    @Override
    public void leaveScene() {
    }

    private void createEmptyFrame(final QuestTask task) {
        final DialogFrame dialog = new TextDialogFrame(715, cameraCenterY - 45, 540, 360, activity, "没有信息！");
        dialog.bind(this, new ICallback() {

            @Override
            public void onCallback() {
                iLeaveCallback.onCallback();
                back();
            }

        });
    }

    private void createDialogFrame(final QuestTask task) {
        final DialogFrame dialog = new TextDialogFrame(715, cameraCenterY - 45, 540, 360, activity, task.getDialog());
        dialog.bind(this, new ICallback() {

            @Override
            public void onCallback() {
                if (TaskUtils.accept()) {
                    dialog.unbind(TaskGuideScene.this);
                    task.setStatus(UserTaskStatus.Started);
                    createTipsFrame(task);
                }
            }

        });
    }

    private void createTipsFrame(final QuestTask task) {
        final DialogFrame dialog = new CommonDialogFrame(715, cameraCenterY - 45, 540, 360, activity, "任务：" + task.getTitle(), task.getTips());
        dialog.bind(this, new ICallback() {

            @Override
            public void onCallback() {
                iLeaveCallback.onCallback();
                back();
            }

        });
    }

    private void createRewardFrame(final QuestTask task) {
        final List<Reward> rewards = TaskUtils.getTaskReward(activity, task.getId());
        final DialogFrame dialog = new CommonDialogFrame(715, cameraCenterY - 45, 540, 360, activity, "任务奖励", "奖励将会发放到你的仓库");
        for (final Reward reward : rewards) {
            final IEntity rewardGrid = new Rectangle(270, 150, 540, 200, vbom);
            rewardGrid.setAlpha(0);
            rewardGrid.setScale(0.8f);
            insertRewardItems(reward.getRewardItems(), rewardGrid);
            dialog.attachChild(rewardGrid);
        }

        dialog.bind(this, new ICallback() {

            @Override
            public void onCallback() {
                if (TaskUtils.complete()) {
                    TaskUtils.refresh();
                }
                back();
                iLeaveCallback.onCallback();
            }

        });
    }

    private void insertRewardItems(final List<RewardItem> rewardItems, final IEntity rewardGrid) {
        final Font amountFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Font itemFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
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
            final RewardItem rewardItem = rewardItems.get(itemIndex);
            final RewardItemType rewardItemType = rewardItem.getType();

            final Text amountText = new Text(95, 25, amountFont, String.format("×%s", rewardItem.getAmount()), vbom);
            amountText.setColor(0XFFAECE01);
            amountText.setX(itemGridEnum.getWidth() - amountText.getWidth() * 0.5f - 10);
            if (rewardItemType == RewardItemType.ArenaTicket) {
                final IEntity ticketGrid = createACImageSprite(itemGridEnum, itemGridInitX + itemWidth * itemIndex, itemGridY);
                final IEntity ticketImg = createACImageSprite(ticketEnum, itemX, itemY);
                final Text itemText = new Text(itemX, itemGridEnum.getHeight() + 25, itemFont, "竞技场门票", vbom);
                itemText.setColor(0XFF330504);
                ticketGrid.attachChild(itemText);
                ticketGrid.attachChild(ticketImg);
                ticketGrid.attachChild(amountText);
                rewardGrid.attachChild(ticketGrid);
            } else if (rewardItemType == RewardItemType.Stamina) {
                final IEntity staminaGrid = createACImageSprite(itemGridEnum, itemGridInitX + itemWidth * itemIndex, itemGridY);
                final IEntity staminaImg = createACImageSprite(staminaEnum, itemX - 10, itemY - 5);
                final Text itemText = new Text(itemX, itemGridEnum.getHeight() + 25, itemFont, "精力药水", vbom);
                itemText.setColor(0XFF330504);
                staminaGrid.attachChild(itemText);
                staminaGrid.attachChild(staminaImg);
                staminaGrid.attachChild(amountText);
                rewardGrid.attachChild(staminaGrid);
            } else if (rewardItemType == RewardItemType.GuildContribution) {
                final IEntity guildContributionGrid = createACImageSprite(itemGridEnum, itemGridInitX + itemWidth * itemIndex, itemGridY);
                final IEntity guildContributionImg = createACImageSprite(guildContributionEnum, itemX, itemY + 3);
                final Text itemText = new Text(itemX, itemGridEnum.getHeight() + 25, itemFont, "公会贡献值", vbom);
                itemText.setColor(0XFFFFE8C6);
                guildContributionGrid.attachChild(itemText);
                guildContributionGrid.attachChild(guildContributionImg);
                guildContributionGrid.attachChild(amountText);
                rewardGrid.attachChild(guildContributionGrid);
            } else if (rewardItemType == RewardItemType.Card) {
                final Card card = rewardItem.getCard();
                final float cardAdjustX = (itemIndex > 0 ? 15 : 0);
                final CardFrame cardSprite = new CardFrame(itemGridInitX + (itemWidth - cardAdjustX) * itemIndex, itemGridY + 15, 110, 165, card, activity);
                amountText.setX(cardSprite.getWidth() + amountText.getWidth() * 0.5f + 5);
                amountText.setColor(0XFF330504);
                cardSprite.attachChild(amountText);
                rewardGrid.attachChild(cardSprite);
                this.registerTouchArea(cardSprite);
            }

        }
    }
}
