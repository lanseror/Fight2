package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.modifier.IModifier;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.BattlePartyFrame;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.HpBar;
import com.fight2.entity.Party;
import com.fight2.entity.battle.BattleRecord;
import com.fight2.entity.battle.SkillApplyParty;
import com.fight2.entity.battle.SkillOperation;
import com.fight2.entity.battle.SkillRecord;
import com.fight2.entity.battle.SkillType;
import com.fight2.util.ArenaUtils;
import com.fight2.util.CardUtils;
import com.fight2.util.ResourceManager;

public class BattleScene extends BaseScene {
    private final float TOP_PARTY_FRAME_Y = this.cameraHeight - TextureEnum.BATTLE_PARTY_TOP.getHeight();
    public final static int CARD_WIDTH = 100;
    public final static int CARD_HEIGHT = 150;
    public final static float FRAME_WIDTH = TextureEnum.BATTLE_PARTY_TOP.getWidth();
    public final static float FRAME_START_X = 90;
    public final static float PARTY_GAP = 10;
    private final Party[] myParties = GameUserSession.getInstance().getPartyInfo().getParties();
    private final Party[] opponentParties;

    private final BattlePartyFrame[] myPartyFrames = new BattlePartyFrame[3];
    private final BattlePartyFrame[] opponentPartyFrames = new BattlePartyFrame[3];
    private final Font font = ResourceManager.getInstance().getFont(FontEnum.Battle);
    private final Text skillText;
    private final Text skillEffectText;
    private final Queue<BattleRecord> battleRecordQueue = new LinkedList<BattleRecord>();
    private final Queue<OnFinishedCallback> battleFinishedCallbackQueue = new LinkedList<OnFinishedCallback>();

    public BattleScene(final GameActivity activity, final int attackPlayerId) throws IOException {
        super(activity);
        this.skillText = new Text(this.cameraCenterX, this.cameraCenterY + 30, font, "技能：", 30, vbom);
        this.skillEffectText = new Text(this.cameraCenterX, this.cameraCenterY - 10, font, "效果：", 30, vbom);
        skillText.setAlpha(0);
        skillEffectText.setAlpha(0);
        this.attachChild(skillText);
        this.attachChild(skillEffectText);
        opponentParties = CardUtils.getPartyByUserId(activity, attackPlayerId).getParties();
        final List<BattleRecord> battleRecords = ArenaUtils.attack(attackPlayerId, activity);
        for (final BattleRecord battleRecord : battleRecords) {
            battleRecordQueue.add(battleRecord);
            battleFinishedCallbackQueue.add(new BattleFinishedCallback());
        }
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.BATTLE_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 140, 250);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Arena);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite tButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 140, 150);
        tButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                // final BattleRecord battleRecord1 = new BattleRecord();
                // battleRecord1.setActionPlayer("Player1");
                // battleRecord1.setAtk(200);
                // battleRecord1.setAtkParty(0);
                // battleRecord1.setDefenceParty(0);
                // final SkillRecord skill = new SkillRecord();
                // skill.setCardIndex(0);
                // skill.setName("一击必杀");
                // skill.setEffect("对敌方团队（全体）造成伤害。");
                // battleRecord1.setSkill(skill);
                // final SkillOperation operationRecord = new SkillOperation();
                // operationRecord.setSign(-1);
                // operationRecord.setPoint(350);
                // operationRecord.setSkillApplyParty(SkillApplyParty.OpponentAll);
                // operationRecord.setSkillType(SkillType.HP);
                // final List<SkillOperation> operations = new ArrayList<SkillOperation>();
                // operations.add(operationRecord);
                // skill.setOperations(operations);
                //
                // final BattleRecord battleRecord2 = new BattleRecord();
                // battleRecord2.setActionPlayer("Player2");
                // battleRecord2.setAtk(355);
                // battleRecord2.setAtkParty(1);
                // battleRecord2.setDefenceParty(0);
                //
                // final OnFinishedCallback battleFinishedCallback1 = new OnFinishedCallback() {
                //
                // @Override
                // public void onFinished(final IEntity pItem) {
                // // TODO Auto-generated method stub
                //
                // }
                //
                // };
                //
                // final OnFinishedCallback battleFinishedCallback2 = new OnFinishedCallback() {
                //
                // @Override
                // public void onFinished(final IEntity pItem) {
                // handleBattleRecord(battleRecord2, battleFinishedCallback1);
                // }
                //
                // };
                // handleBattleRecord(battleRecord1, battleFinishedCallback2);
                if (!battleRecordQueue.isEmpty()) {
                    handleBattleRecord(battleRecordQueue.poll(), battleFinishedCallbackQueue.poll());
                }
            }
        });
        this.attachChild(tButton);
        this.registerTouchArea(tButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        updateScene();
    }

    private class BattleFinishedCallback implements OnFinishedCallback {

        @Override
        public void onFinished(final IEntity pItem) {
            final BattleRecord battleRecord = battleRecordQueue.poll();
            if (battleRecord != null) {
                handleBattleRecord(battleRecord, battleFinishedCallbackQueue.poll());
            }
        }

    }

    private void handleBattleRecord(final BattleRecord battleRecord, final OnFinishedCallback battleFinishedCallback) {
        final String actionPlayer = battleRecord.getActionPlayer();
        final int actionPartyIndex = battleRecord.getAtkParty();
        final int defencePartyIndex = battleRecord.getDefenceParty();
        final int atk = battleRecord.getAtk();
        final SkillRecord skill = battleRecord.getSkill();
        final boolean isMyAction = "Player1".equals(actionPlayer);

        final BattlePartyFrame[] actionPartyFrames = isMyAction ? myPartyFrames : opponentPartyFrames;
        final BattlePartyFrame[] defencePartyFrames = isMyAction ? opponentPartyFrames : myPartyFrames;
        final BattlePartyFrame actionPartyFrame = actionPartyFrames[actionPartyIndex];
        final BattlePartyFrame defencePartyFrame = defencePartyFrames[defencePartyIndex];

        final OnFinishedCallback attackFinishedCallback = new OnFinishedCallback() {

            @Override
            public void onFinished(final IEntity pItem) {
                revertParties(actionPartyFrames, actionPartyIndex, battleFinishedCallback);
            }

        };
        final OnFinishedCallback attackHitCallback = new OnFinishedCallback() {

            @Override
            public void onFinished(final IEntity pItem) {
                final HpBar hpBar = defencePartyFrame.getHpBar();
                final int hp = hpBar.getCurrentPoint();
                final int defence = defencePartyFrame.getDefence();
                final int changeDefence = defence - atk;
                if (changeDefence > 0) {
                    defencePartyFrame.setDefence(changeDefence);
                } else {
                    defencePartyFrame.setDefence(0);
                    final int changeHp = hp + changeDefence;
                    hpBar.setCurrentPoint(changeHp < 0 ? 0 : changeHp);
                }

            }

        };

        final OnFinishedCallback prepareFinishedCallback = new OnFinishedCallback() {
            @Override
            public void onFinished(final IEntity pItem) {
                if (skill != null) {
                    final int cardIndex = skill.getCardIndex();
                    final OnFinishedCallback onUpCallback = new OnFinishedCallback() {

                        @Override
                        public void onFinished(final IEntity pItem) {
                            skillText.setText("技能：" + skill.getName());
                            skillEffectText.setText("效果：" + skill.getEffect());
                            final IEntityModifier showModifier = new AlphaModifier(0.5f, 0, 1);
                            final IEntityModifier hideModifier = new AlphaModifier(0.5f, 1, 0);
                            final IEntityModifier delayModifier = new DelayModifier(2f);
                            activity.runOnUpdateThread(new Runnable() {

                                @Override
                                public void run() {
                                    skillText.clearEntityModifiers();
                                    skillEffectText.clearEntityModifiers();
                                    skillText.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier));
                                    skillEffectText.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier));
                                }

                            });
                        }

                    };
                    final OnFinishedCallback onFinishedCallback = new OnFinishedCallback() {

                        @Override
                        public void onFinished(final IEntity pItem) {
                            handleSkillOperations(actionPartyFrame, skill, isMyAction);
                            attack(actionPartyFrame, defencePartyFrame, attackHitCallback, attackFinishedCallback);
                        }

                    };
                    actionPartyFrame.useSkill(cardIndex - 1, onUpCallback, onFinishedCallback);
                } else {
                    attack(actionPartyFrame, defencePartyFrame, attackHitCallback, attackFinishedCallback);
                }

            }
        };
        // attack(myPartyFrames[2], opponentPartyFrames[0], attackFinishedCallback);
        prepareAttack(actionPartyFrames, actionPartyIndex, defencePartyIndex, prepareFinishedCallback);
        // myPartyFrames[0].setAtk(503);
        // opponentPartyFrames[0].setAtk(3544);
    }

    private void handleSkillOperations(final BattlePartyFrame actionParty, final SkillRecord skill, final boolean isMyAction) {
        final List<SkillOperation> operations = skill.getOperations();
        for (final SkillOperation operation : operations) {
            final SkillType skillType = operation.getSkillType();
            final int sign = operation.getSign();
            final int changePoint = operation.getPoint() * sign;
            final List<BattlePartyFrame> applyParties = this.getApplyParties(actionParty, operation.getSkillApplyParty(), isMyAction);

            for (final BattlePartyFrame applyParty : applyParties) {
                switch (skillType) {
                    case HP:
                        if (changePoint < 0) {
                            final int changeDefence = applyParty.getDefence() + changePoint;
                            if (changeDefence > 0) {
                                applyParty.setDefence(changeDefence);
                            } else {
                                applyParty.setDefence(0);
                                final int changeHp = applyParty.getHp() + changeDefence;
                                applyParty.setHp(changeHp < 0 ? 0 : changeHp);
                            }

                        } else {
                            applyParty.setHp(applyParty.getHp() + changePoint);
                        }
                        break;
                    case ATK:
                        applyParty.setAtk(applyParty.getAtk() + changePoint);
                        break;
                    case Defence:
                        applyParty.setDefence(applyParty.getDefence() + changePoint);
                        break;
                    case Skip:
                        // TODO
                        break;
                }
            }
        }

    }

    private List<BattlePartyFrame> getApplyParties(final BattlePartyFrame selfParty, final SkillApplyParty skillApplyParty, final boolean isMyAction) {
        final BattlePartyFrame[] selfParties = isMyAction ? myPartyFrames : opponentPartyFrames;
        final BattlePartyFrame[] opponentParties = isMyAction ? opponentPartyFrames : myPartyFrames;
        final List<BattlePartyFrame> applyParties = new ArrayList<BattlePartyFrame>();
        switch (skillApplyParty) {
            case Self:
                applyParties.add(selfParty);
                break;
            case Opponent:
                for (final BattlePartyFrame party : opponentParties) {
                    if (party.getHpBar().getCurrentPoint() > 0) {
                        applyParties.add(party);
                        break;
                    }
                }
                break;
            case Leader:
                final BattlePartyFrame leader = selfParties[0];
                if (leader != null) {
                    applyParties.add(leader);
                }
                break;
            case OpponentLeader:
                final BattlePartyFrame opponentLeader = opponentParties[0];
                if (opponentLeader != null) {
                    applyParties.add(opponentLeader);
                }
                break;
            case SelfAll:
                for (final BattlePartyFrame party : selfParties) {
                    if (party.getHpBar().getCurrentPoint() > 0) {
                        applyParties.add(party);
                    }
                }
                break;
            case OpponentAll:
                for (final BattlePartyFrame party : opponentParties) {
                    if (party.getHpBar().getCurrentPoint() > 0) {
                        applyParties.add(party);
                    }
                }
                break;
            default:
                applyParties.add(selfParty);
        }
        return applyParties;
    }

    @Override
    public void updateScene() {
        final float width = TextureEnum.BATTLE_PARTY_BOTTOM.getWidth();

        for (int i = 0; i < myParties.length; i++) {
            final Party party = myParties[i];
            if (party.getHp() <= 0) {
                continue;
            }
            final BattlePartyFrame partyContainer = createPartyContainer(party, FRAME_START_X + (width + PARTY_GAP) * i, 0, true);
            this.attachChild(partyContainer);
            myPartyFrames[i] = partyContainer;
        }

        for (int i = 0; i < opponentParties.length; i++) {
            final Party party = opponentParties[i];
            if (party.getHp() <= 0) {
                continue;
            }
            final BattlePartyFrame partyContainer = createOpponentPartyContainer(party, FRAME_START_X + (width + PARTY_GAP) * i, TOP_PARTY_FRAME_Y - 67, false);
            this.attachChild(partyContainer);
            opponentPartyFrames[i] = partyContainer;
        }
    }

    public void prepareAttack(final BattlePartyFrame[] actionPartyFrames, final int actionPartyFrameIndex, final int defencePartyFrameIndex,
            final OnFinishedCallback onFinishedCallback) {
        final float frameHeight = actionPartyFrames[actionPartyFrameIndex].getHeight();
        final float zoomInscale = 1.3f;
        final float zoomOutScale = (3 - zoomInscale) / 2;
        final float zoomInWidth = FRAME_WIDTH * zoomInscale;
        final float zoomOutWidth = FRAME_WIDTH * zoomOutScale;
        final float zoomInHeight = frameHeight * zoomInscale;
        final float zoomOutHeight = frameHeight * zoomOutScale;

        float actionX = 0;
        if (actionPartyFrameIndex == 0) {
            actionX = zoomInWidth * 0.5f;
        } else if (actionPartyFrameIndex == 1) {
            actionX = FRAME_WIDTH * 1.5f + PARTY_GAP;
        } else {
            actionX = (zoomOutWidth + PARTY_GAP) * 2 + zoomInWidth * 0.5f;
        }

        for (int i = 0; i < 3; i++) {
            final BattlePartyFrame partyFrame = actionPartyFrames[i];
            if (partyFrame == null) {
                continue;
            }
            float changeX = 0;
            float changeY = 0;
            float chaneScale = 0;
            if (i < actionPartyFrameIndex) {
                changeX = actionX - zoomInWidth * 0.5f - ((PARTY_GAP + zoomOutWidth) * (actionPartyFrameIndex - i - 1)) - PARTY_GAP - zoomOutWidth * 0.5f;
                changeY = zoomOutHeight * 0.5f;
                chaneScale = zoomOutScale;
            } else if (i == actionPartyFrameIndex) {
                changeX = actionX;
                changeY = zoomInHeight * 0.5f;
                chaneScale = zoomInscale;
            } else {
                changeX = actionX + zoomInWidth * 0.5f + (PARTY_GAP + zoomOutWidth) * (i - actionPartyFrameIndex - 1) + PARTY_GAP + zoomOutWidth * 0.5f;
                changeY = zoomOutHeight * 0.5f;
                chaneScale = zoomOutScale;
            }

            if (!partyFrame.isBottom()) {
                changeY = this.cameraHeight - changeY;
            }
            final float duration = 0.2f;
            final IEntityModifier scaleModifier = new ScaleModifier(duration, 1, chaneScale);
            final IEntityModifier moveModifier = new MoveModifier(duration, partyFrame.getInitX(), partyFrame.getInitY(), FRAME_START_X + changeX, changeY);
            final IEntityModifier modifier = new ParallelEntityModifier(scaleModifier, moveModifier);

            if (i == actionPartyFrameIndex) {
                moveModifier.addModifierListener(new ModifierFinishedListener(onFinishedCallback));
            }

            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    partyFrame.clearEntityModifiers();
                    partyFrame.registerEntityModifier(modifier);
                }
            });
        }

    }

    private void attack(final BattlePartyFrame actionPartyFrame, final BattlePartyFrame defencePartyFrame, final OnFinishedCallback onHitCallback,
            final OnFinishedCallback onFinishedCallback) {
        final float initScale = actionPartyFrame.getScaleX();
        final float initX = actionPartyFrame.getX();
        final float initY = actionPartyFrame.getY();
        final float attackDuration = 0.05f;
        final float backDuration = 0.5f;
        actionPartyFrame.setZIndex(10);
        this.sortChildren();

        final float adjustY = actionPartyFrame.isBottom() ? -140 : 137;
        final IEntityModifier attackScaleModifier = new ScaleModifier(attackDuration, initScale, 1, new ModifierFinishedListener(onHitCallback));
        final IEntityModifier attackMoveModifier = new MoveModifier(attackDuration, initX, initY, defencePartyFrame.getX(), defencePartyFrame.getY() + adjustY);
        final IEntityModifier attackModifier = new ParallelEntityModifier(attackScaleModifier, attackMoveModifier);

        final IEntityModifierListener backFinishListener = new ModifierFinishedListener(new OnFinishedCallback() {
            @Override
            public void onFinished(final IEntity pItem) {
                pItem.setZIndex(IEntity.ZINDEX_DEFAULT);
                BattleScene.this.sortChildren();
                onFinishedCallback.onFinished(pItem);
            }

        });

        final IEntityModifier backScaleModifier = new ScaleModifier(backDuration, 1, initScale, backFinishListener);
        final IEntityModifier backMoveModifier = new MoveModifier(backDuration, defencePartyFrame.getX(), defencePartyFrame.getY() + adjustY, initX, initY);

        final IEntityModifier backModifier = new ParallelEntityModifier(backScaleModifier, backMoveModifier);

        final IEntityModifier actionModifier = new SequenceEntityModifier(new DelayModifier(0.8f), attackModifier, new DelayModifier(0.1f), backModifier);
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                actionPartyFrame.clearEntityModifiers();
                actionPartyFrame.registerEntityModifier(actionModifier);
            }
        });

    }

    private void revertParties(final BattlePartyFrame[] actionPartyFrames, final int actionPartyIndex, final OnFinishedCallback onFinishedCallback) {
        final float duration = 0.2f;
        for (int i = 0; i < 3; i++) {
            final BattlePartyFrame partyFrame = actionPartyFrames[i];
            if (partyFrame == null) {
                continue;
            }
            final IEntityModifier scaleModifier = new ScaleModifier(duration, partyFrame.getScaleX(), 1);
            final IEntityModifier moveModifier = new MoveModifier(duration, partyFrame.getX(), partyFrame.getY(), partyFrame.getInitX(), partyFrame.getInitY());
            final IEntityModifier modifier = new ParallelEntityModifier(scaleModifier, moveModifier);
            if (i == actionPartyIndex) {
                moveModifier.addModifierListener(new ModifierFinishedListener(onFinishedCallback));
            }
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    partyFrame.clearEntityModifiers();
                    partyFrame.registerEntityModifier(modifier);
                }
            });
        }
    }

    private BattlePartyFrame createPartyContainer(final Party party, final float x, final float y, final boolean isBottom) {
        return new BattlePartyFrame(x, y, party, activity, isBottom);
    }

    private BattlePartyFrame createOpponentPartyContainer(final Party party, final float x, final float y, final boolean isBottom) {
        return new BattlePartyFrame(x, y, party, activity, isBottom);
    }

    public static class ModifierFinishedListener implements IEntityModifierListener {
        private final OnFinishedCallback onFinishedCallback;

        public ModifierFinishedListener(final OnFinishedCallback onFinishedCallback) {
            this.onFinishedCallback = onFinishedCallback;
        }

        @Override
        public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
        }

        @Override
        public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
            onFinishedCallback.onFinished(pItem);
        }
    }

    public static interface OnFinishedCallback {
        public void onFinished(final IEntity pItem);
    }

}
