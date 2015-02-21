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
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.IModifier;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.ComboSkill;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.battle.BattleRecord;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.battle.BattleType;
import com.fight2.entity.battle.RevivalRecord;
import com.fight2.entity.battle.RevivalRecord.RevivalType;
import com.fight2.entity.battle.SkillApplyParty;
import com.fight2.entity.battle.SkillOperation;
import com.fight2.entity.battle.SkillRecord;
import com.fight2.entity.battle.SkillType;
import com.fight2.entity.engine.BattlePartyFrame;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.HpBar;
import com.fight2.util.ArenaUtils;
import com.fight2.util.F2MusicManager;
import com.fight2.util.F2SoundManager;
import com.fight2.util.IRCallback;
import com.fight2.util.MineUtils;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TaskUtils;
import com.fight2.util.TiledTextureFactory;

public class BattleScene extends BaseScene {
    private final float TOP_PARTY_FRAME_Y = this.cameraHeight - TextureEnum.BATTLE_PARTY_TOP.getHeight();
    public final static String REVIVAL_STR = "团队死亡时有机会以%s%%的生命值复活。";
    public final static int CARD_WIDTH = 100;
    public final static int CARD_HEIGHT = 150;
    public final static float FRAME_WIDTH = TextureEnum.BATTLE_PARTY_TOP.getWidth();
    public final static float FRAME_START_X = 90;
    public final static float PARTY_GAP = 10;
    public final static float SKILL_FRAME_CENTER_X = TextureEnum.BATTLE_SKILL_FRAME.getWidth() * 0.5f;
    private final Party[] attackerParties;
    private final Party[] defenderParties;

    private final BattlePartyFrame[] myPartyFrames = new BattlePartyFrame[3];
    private final BattlePartyFrame[] opponentPartyFrames = new BattlePartyFrame[3];
    private final Font font = ResourceManager.getInstance().newFont(FontEnum.Default, 32, 256);
    private final Text skillTextTitle;
    private final Text skillEffectTextTitle;
    private final Text skillText;
    private final Text skillEffectText;
    private final Queue<BattleRecord> battleRecordQueue = new LinkedList<BattleRecord>();
    private final Queue<OnFinishedCallback> battleFinishedCallbackQueue = new LinkedList<OnFinishedCallback>();
    private final Sprite winImage;
    private final Sprite loseImage;
    private final boolean isWinner;
    private final Sprite skipSprite;
    private final BattleResult battleResult;
    private final BattleType battleType;
    private final AnimatedSprite attackEffectSprite;
    private final AnimatedSprite[] cureEffectSprites = new AnimatedSprite[3];
    private final AnimatedSprite[] confuseEffectSprites = new AnimatedSprite[3];
    private final AnimatedSprite[] magicEffectSprites = new AnimatedSprite[3];
    private final AnimatedSprite[] goodEffectSprites = new AnimatedSprite[3];
    private final Sprite battleSkillFrame;
    private final Sprite revivalSkillFrame;
    private final Sprite hitRedEffect;
    private final Text revivalText;

    public BattleScene(final GameActivity activity, final int attackPlayerId, final Party[] opponentParties, final BattleType battleType) throws IOException {
        super(activity);
        this.battleType = battleType;
        this.battleSkillFrame = this.createACImageSprite(TextureEnum.BATTLE_SKILL_FRAME, this.cameraCenterX, this.cameraCenterY + 15);
        battleSkillFrame.setAlpha(0);
        battleSkillFrame.setZIndex(500);
        final float skillFrameYCenter = battleSkillFrame.getHeight() * 0.5f;
        this.skillTextTitle = new Text(SKILL_FRAME_CENTER_X - 120, skillFrameYCenter + 37, font, "技能：", vbom);
        skillTextTitle.setColor(0XFFFACC62);
        skillTextTitle.setAlpha(0);
        this.skillEffectTextTitle = new Text(SKILL_FRAME_CENTER_X - 300, skillFrameYCenter - 25, font, "效果：", vbom);
        skillEffectTextTitle.setColor(0XFFFACC62);
        skillEffectTextTitle.setAlpha(0);
        this.skillText = new Text(SKILL_FRAME_CENTER_X, skillFrameYCenter + 37, font, "", 30, vbom);
        this.skillEffectText = new Text(SKILL_FRAME_CENTER_X, skillFrameYCenter - 25, font, "", 100, vbom);
        skillText.setAlpha(0);
        skillEffectText.setAlpha(0);
        battleSkillFrame.attachChild(skillTextTitle);
        battleSkillFrame.attachChild(skillEffectTextTitle);
        battleSkillFrame.attachChild(skillText);
        battleSkillFrame.attachChild(skillEffectText);
        this.attachChild(battleSkillFrame);
        this.revivalSkillFrame = this.createACImageSprite(TextureEnum.BATTLE_SKILL_REVIVAL, 0, 0);
        revivalSkillFrame.setAlpha(0);
        revivalSkillFrame.setZIndex(500);
        this.attachChild(revivalSkillFrame);
        final Font revivalFont = ResourceManager.getInstance().newFont(FontEnum.Default, 26, 256);
        this.revivalText = new Text(revivalSkillFrame.getWidth() * 0.5f, revivalSkillFrame.getHeight() * 0.5f, revivalFont, REVIVAL_STR, new TextOptions(
                AutoWrap.LETTERS, 330, HorizontalAlign.CENTER), vbom);
        revivalText.setAlpha(0);
        revivalSkillFrame.attachChild(revivalText);

        winImage = this.createACImageSprite(TextureEnum.BATTLE_WIN, this.cameraCenterX, this.cameraCenterY);
        loseImage = this.createACImageSprite(TextureEnum.BATTLE_LOSE, this.cameraCenterX, this.cameraCenterY);
        winImage.setAlpha(0);
        loseImage.setAlpha(0);
        this.attachChild(winImage);
        this.attachChild(loseImage);

        this.hitRedEffect = this.createACImageSprite(TextureEnum.BATTLE_EFFECT_HIT_RED, this.cameraCenterX, this.cameraCenterY);
        this.attachChild(hitRedEffect);
        hitRedEffect.setAlpha(0);

        switch (battleType) {
            case Arena:
                battleResult = ArenaUtils.attack(attackPlayerId, activity);
                break;
            case Quest:
                battleResult = QuestUtils.attack(attackPlayerId, activity);
                break;
            case Task:
                battleResult = TaskUtils.attack(activity);
                break;
            case Mine:
                battleResult = MineUtils.attack(activity);
                break;
            default:
                battleResult = ArenaUtils.attack(attackPlayerId, activity);
        }

        final Party[] myParties = GameUserSession.getInstance().getPartyInfo().getParties();
        final Party[] attackerParties = battleResult.getAttackerParties();
        final Party[] defenderParties = battleResult.getDefenderParties();
        for (int i = 0; i < 3; i++) {
            final Party myParty = myParties[i];
            final Party attackerParty = attackerParties[i];
            attackerParty.setCards(myParty.getCards());
            attackerParty.setComboSkills(new ArrayList<ComboSkill>(myParty.getComboSkills()));

            final Party opponentParty = opponentParties[i];
            final Party defenderParty = defenderParties[i];
            defenderParty.setCards(opponentParty.getCards());
            defenderParty.setComboSkills(new ArrayList<ComboSkill>(opponentParty.getComboSkills()));
        }
        this.attackerParties = attackerParties;
        this.defenderParties = defenderParties;

        isWinner = battleResult.isWinner();
        final List<BattleRecord> battleRecords = battleResult.getBattleRecord();
        for (final BattleRecord battleRecord : battleRecords) {
            battleRecordQueue.add(battleRecord);
            battleFinishedCallbackQueue.add(new BattleFinishedCallback());
        }

        init();

        skipSprite = createALBImageSprite(TextureEnum.BATTLE_SKIP, this.simulatedRightX - 142, 250, new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                skipSprite.setVisible(false);
                battleRecordQueue.clear();
            }
        });
        this.attachChild(skipSprite);
        this.registerTouchArea(skipSprite);

        final ITiledTextureRegion attackTiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.ATTACK_EFFECT);
        this.attackEffectSprite = new AnimatedSprite(0, 0, attackTiledTextureRegion, vbom);
        attackEffectSprite.setVisible(false);
        attackEffectSprite.setZIndex(1000);

        final ITiledTextureRegion cureTiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.CURE_EFFECT);
        for (int i = 0; i < cureEffectSprites.length; i++) {
            final AnimatedSprite cureEffectSprite = new AnimatedSprite(0, 0, cureTiledTextureRegion, vbom);
            cureEffectSprite.setVisible(false);
            cureEffectSprite.setZIndex(1000);
            cureEffectSprites[i] = cureEffectSprite;
        }
        final ITiledTextureRegion confuseTiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.CONFUSE_EFFECT);
        for (int i = 0; i < confuseEffectSprites.length; i++) {
            final AnimatedSprite confuseEffectSprite = new AnimatedSprite(0, 0, confuseTiledTextureRegion, vbom);
            confuseEffectSprite.setVisible(false);
            confuseEffectSprite.setZIndex(1000);
            confuseEffectSprites[i] = confuseEffectSprite;
        }
        final ITiledTextureRegion magicTiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.BATTLE_MAGIC_ATTACK_EFFECT);
        for (int i = 0; i < magicEffectSprites.length; i++) {
            final AnimatedSprite magicEffectSprite = new AnimatedSprite(0, 0, magicTiledTextureRegion, vbom);
            magicEffectSprite.setVisible(false);
            magicEffectSprite.setZIndex(1000);
            magicEffectSprites[i] = magicEffectSprite;
        }
        final ITiledTextureRegion goodTiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.BATTLE_SKILL_GOOD);
        for (int i = 0; i < goodEffectSprites.length; i++) {
            final AnimatedSprite goodEffectSprite = new AnimatedSprite(0, 0, goodTiledTextureRegion, vbom);
            goodEffectSprite.setVisible(false);
            goodEffectSprite.setZIndex(1000);
            goodEffectSprites[i] = goodEffectSprite;
        }

    }

    @Override
    protected void playAnimation() {
        if (!battleRecordQueue.isEmpty()) {
            handleBattleRecord(battleRecordQueue.poll(), battleFinishedCallbackQueue.poll());
        }
    }

    @Override
    protected void init() throws IOException {
        final TextureEnum bgTextureEnum = battleType == BattleType.Arena ? TextureEnum.BATTLE_ARENA_BG : TextureEnum.BATTLE_QUEST_BG;
        final Sprite bgSprite = createALBImageSprite(bgTextureEnum, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        final float width = TextureEnum.BATTLE_PARTY_BOTTOM.getWidth();

        for (int i = 0; i < attackerParties.length; i++) {
            final Party party = attackerParties[i];
            if (party.getHp() <= 0) {
                continue;
            }
            final BattlePartyFrame partyContainer = createPartyContainer(party, FRAME_START_X + (width + PARTY_GAP) * i, 0, true);
            this.attachChild(partyContainer);
            myPartyFrames[i] = partyContainer;
        }

        for (int i = 0; i < defenderParties.length; i++) {
            final Party party = defenderParties[i];
            if (party.getHp() <= 0) {
                continue;
            }
            final BattlePartyFrame partyContainer = createOpponentPartyContainer(party, FRAME_START_X + (width + PARTY_GAP) * i, TOP_PARTY_FRAME_Y - 67, false);
            this.attachChild(partyContainer);
            opponentPartyFrames[i] = partyContainer;
        }
        this.attachChild(attackEffectSprite);
        for (int i = 0; i < cureEffectSprites.length; i++) {
            this.attachChild(cureEffectSprites[i]);
        }
        for (int i = 0; i < confuseEffectSprites.length; i++) {
            this.attachChild(confuseEffectSprites[i]);
        }
        for (int i = 0; i < magicEffectSprites.length; i++) {
            this.attachChild(magicEffectSprites[i]);
        }
        for (int i = 0; i < goodEffectSprites.length; i++) {
            this.attachChild(goodEffectSprites[i]);
        }
    }

    private void showBattleResult() {
        skipSprite.setVisible(false);
        F2MusicManager.getInstance().stopMusic();
        F2SoundManager.getInstance().play(isWinner ? SoundEnum.BATTLE_WIN : SoundEnum.BATTLE_LOSE);
        final IEntityModifierListener hideFinishListener = new ModifierFinishedListener(new OnFinishedCallback() {
            @Override
            public void onFinished(final IEntity pItem) {
                if (battleType == BattleType.Task) {
                    final BaseScene scene = ResourceManager.getInstance().getCurrentScene();
                    scene.clearChildScene();
                    ResourceManager.getInstance().setChildScene(scene, new IRCallback<BaseScene>() {
                        @Override
                        public BaseScene onCallback() {
                            try {
                                return new TaskBattleResultScene(battleResult, activity);
                            } catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } else if (battleType == BattleType.Mine) {
                    final BaseScene scene = ResourceManager.getInstance().getCurrentScene();
                    ResourceManager.getInstance().setChildScene(scene, new IRCallback<BaseScene>() {
                        @Override
                        public BaseScene onCallback() {
                            try {
                                return new MineBattleResultScene(battleResult, activity);
                            } catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } else {
                    ResourceManager.getInstance().setCurrentScene(null, new IRCallback<BaseScene>() {
                        @Override
                        public BaseScene onCallback() {
                            try {
                                return new BattleResultScene(battleResult, activity);
                            } catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        });

        final IEntityModifier showModifier = new AlphaModifier(2, 0, 1);
        final IEntityModifier hideModifier = new AlphaModifier(2, 1, 0, hideFinishListener);
        final IEntityModifier battleResultModifier = new SequenceEntityModifier(showModifier, new DelayModifier(1.5f), hideModifier);
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                if (isWinner) {
                    winImage.registerEntityModifier(battleResultModifier);
                } else {
                    loseImage.registerEntityModifier(battleResultModifier);
                }
            }

        });
    }

    private class BattleFinishedCallback implements OnFinishedCallback {

        @Override
        public void onFinished(final IEntity pItem) {
            final BattleRecord battleRecord = battleRecordQueue.poll();
            if (battleRecord != null) {
                handleBattleRecord(battleRecord, battleFinishedCallbackQueue.poll());
            } else {
                showBattleResult();
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
        final List<RevivalRecord> revivalRecords = battleRecord.getRevivalRecords();
        final List<RevivalRecord> afterSkillRevivalRecords = new ArrayList<RevivalRecord>(3);
        final List<RevivalRecord> afterAttackRevivalRecords = new ArrayList<RevivalRecord>(3);
        for (final RevivalRecord revivalRecord : revivalRecords) {
            if (revivalRecord.getType() == RevivalType.AfterSkill) {
                afterSkillRevivalRecords.add(revivalRecord);
            } else if (revivalRecord.getType() == RevivalType.AfterAttack) {
                afterAttackRevivalRecords.add(revivalRecord);
            }
        }

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
                F2SoundManager.getInstance().play(SoundEnum.BATTLE_HIT);
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

                if (!isMyAction) {
                    final IEntityModifier hitEffectShowModifier = new AlphaModifier(0.05f, 0, 1);
                    final IEntityModifier hitEffectHideModifier = new AlphaModifier(0.1f, 1, 0.5f);
                    final IEntityModifier hitEffectShowModifier2 = new AlphaModifier(0.14f, 0.5f, 1);
                    final IEntityModifier hitEffectHideModifier2 = new AlphaModifier(0.03f, 1, 0);
                    final IEntityModifier hitEffectModifier = new SequenceEntityModifier(hitEffectShowModifier, new DelayModifier(0.08f),
                            hitEffectHideModifier, hitEffectShowModifier2, hitEffectHideModifier2);
                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            hitRedEffect.clearEntityModifiers();
                            hitRedEffect.registerEntityModifier(hitEffectModifier);
                        }

                    });
                }
                defencePartyFrame.beenHit();

                // attack140, cure 160.
                attackEffectSprite.setVisible(true);
                final float attackEffectOffsetY = isMyAction ? -65 : 5;
                attackEffectSprite.setPosition(defencePartyFrame.getX(), defencePartyFrame.getY() + attackEffectOffsetY);
                attackEffectSprite.animate(125, false, new IAnimationListener() {

                    @Override
                    public void onAnimationStarted(final AnimatedSprite pAnimatedSprite, final int pInitialLoopCount) {

                    }

                    @Override
                    public void onAnimationFrameChanged(final AnimatedSprite pAnimatedSprite, final int pOldFrameIndex, final int pNewFrameIndex) {

                    }

                    @Override
                    public void onAnimationLoopFinished(final AnimatedSprite pAnimatedSprite, final int pRemainingLoopCount, final int pInitialLoopCount) {

                    }

                    @Override
                    public void onAnimationFinished(final AnimatedSprite pAnimatedSprite) {
                        attackEffectSprite.setVisible(false);
                    }

                });
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
                            skillText.setText(skill.getName());
                            skillEffectText.setText(skill.getEffect());
                            leftAlignEntity(skillText, SKILL_FRAME_CENTER_X - 35);
                            rightAlignEntity(skillEffectTextTitle, skillEffectText.getX() - skillEffectText.getWidth() * 0.5f - 20);
                            final IEntityModifier showModifier = new AlphaModifier(0.6f, 0, 1);
                            final IEntityModifier hideModifier = new AlphaModifier(0.6f, 1, 0);
                            final IEntityModifier delayModifier = new DelayModifier(2f);
                            activity.runOnUpdateThread(new Runnable() {

                                @Override
                                public void run() {
                                    skillText.clearEntityModifiers();
                                    skillText.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier));
                                    skillTextTitle.clearEntityModifiers();
                                    skillTextTitle.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier));
                                    skillEffectText.clearEntityModifiers();
                                    skillEffectText.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier));
                                    skillEffectTextTitle.clearEntityModifiers();
                                    skillEffectTextTitle.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier));
                                    battleSkillFrame.clearEntityModifiers();
                                    battleSkillFrame.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier));
                                }

                            });

                        }

                    };
                    final OnFinishedCallback skillOpFinishedCallback = new OnFinishedCallback() {

                        @Override
                        public void onFinished(final IEntity pItem) {
                            attack(actionPartyFrame, defencePartyFrame, attackHitCallback, attackFinishedCallback, isMyAction, afterAttackRevivalRecords);
                        }

                    };
                    final OnFinishedCallback useSkillFinishedCallback = new OnFinishedCallback() {

                        @Override
                        public void onFinished(final IEntity pItem) {
                            handleSkillOperations(actionPartyFrame, skill, isMyAction, skillOpFinishedCallback, afterSkillRevivalRecords);
                        }
                    };
                    actionPartyFrame.useSkill(cardIndex - 1, onUpCallback, useSkillFinishedCallback);
                } else {
                    attack(actionPartyFrame, defencePartyFrame, attackHitCallback, attackFinishedCallback, isMyAction, afterAttackRevivalRecords);
                }

            }
        };
        // attack(myPartyFrames[2], opponentPartyFrames[0], attackFinishedCallback);
        prepareAttack(actionPartyFrames, actionPartyIndex, defencePartyIndex, prepareFinishedCallback);
        // myPartyFrames[0].setAtk(503);
        // opponentPartyFrames[0].setAtk(3544);
    }

    private void handleSkillOperations(final BattlePartyFrame actionParty, final SkillRecord skill, final boolean isMyAction,
            final OnFinishedCallback onFinishedCallback, final List<RevivalRecord> afterSkillRevivalRecords) {
        final List<SkillOperation> operations = skill.getOperations();
        for (final SkillOperation operation : operations) {
            final SkillType skillType = operation.getSkillType();
            final int sign = operation.getSign();
            final int changePoint = operation.getPoint() * sign;
            final List<BattlePartyFrame> applyParties = this.getApplyParties(actionParty, operation.getSkillApplyParty(), isMyAction);

            boolean isSkillCure = false;
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
                            useSkillMagicAttack(applyParty, isMyAction);
                        } else {
                            applyParty.setHp(applyParty.getHp() + changePoint);
                            isSkillCure = true;
                            useSkillCure(applyParty, isMyAction);
                        }
                        break;
                    case ATK:
                        applyParty.setAtk(applyParty.getAtk() + changePoint);
                        if (changePoint < 0) {
                            useSkillConfuse(applyParty, isMyAction);
                        } else {
                            useSkillGoodMagic(applyParty, isMyAction);
                        }
                        break;
                    case Defence:
                        applyParty.setDefence(applyParty.getDefence() + changePoint);
                        break;
                    case Skip:
                        // TODO
                        break;
                }
            }
            if (isSkillCure) {
                F2SoundManager.getInstance().play(SoundEnum.BATTLE_CURE);
            }

        }

        final OnFinishedCallback preReviveFinishCallback = createPreReviveCallback(actionParty, isMyAction, onFinishedCallback, afterSkillRevivalRecords);

        final IEntityModifier delayModifier = new DelayModifier(0.75f, new IEntityModifierListener() {

            @Override
            public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
            }

            @Override
            public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                preReviveFinishCallback.onFinished(actionParty);
            }

        });
        actionParty.registerEntityModifier(delayModifier);
    }

    private OnFinishedCallback createPreReviveCallback(final BattlePartyFrame actionParty, final boolean isMyAction,
            final OnFinishedCallback onFinishedCallback, final List<RevivalRecord> revivalRecords) {

        final BattlePartyFrame[] defencePartyFrames = isMyAction ? opponentPartyFrames : myPartyFrames;
        if (revivalRecords.size() > 0) {
            final OnFinishedCallback preReviveFinishCallback = new OnFinishedCallback() {
                @Override
                public void onFinished(final IEntity pItem) {
                    for (int i = 0; i < revivalRecords.size(); i++) {
                        final RevivalRecord revivalRecord = revivalRecords.get(i);
                        final BattlePartyFrame reviveParty = defencePartyFrames[revivalRecord.getPartyNumber() - 1];
                        if (i == 0) {
                            handleRevival(reviveParty, revivalRecord, isMyAction, onFinishedCallback);
                        } else {
                            handleRevival(reviveParty, revivalRecord, isMyAction, null);
                        }

                    }
                }
            };
            return preReviveFinishCallback;
        } else {
            return onFinishedCallback;
        }
    }

    private void handleRevival(final BattlePartyFrame applyParty, final RevivalRecord skill, final boolean isMyAction,
            final OnFinishedCallback onFinishedCallback) {
        final int point = skill.getPoint();
        final int changePoint = point * applyParty.getHpBar().getFullHp() / 100;
        final IEntityModifier hpDelayModifier = new DelayModifier(2.5f, new IEntityModifierListener() {

            @Override
            public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
            }

            @Override
            public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                applyParty.setHp(changePoint);
            }

        });
        applyParty.useCombo(skill.getComboId());
        revivalText.setText(String.format(REVIVAL_STR, point));
        final float attackEffectOffsetY = isMyAction ? -118 : 118;
        revivalSkillFrame.setPosition(applyParty.getX(), applyParty.getY() + attackEffectOffsetY);
        final IEntityModifier showModifier = new AlphaModifier(0.6f, 0, 1);
        final IEntityModifier hideModifier = new AlphaModifier(0.6f, 1, 0);
        final IEntityModifier delayModifier = new DelayModifier(2f);
        final IEntityModifier finishDelayModifier = new DelayModifier(2f, new IEntityModifierListener() {
            @Override
            public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
            }

            @Override
            public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                if (onFinishedCallback != null) {
                    onFinishedCallback.onFinished(pItem);
                }
            }
        });
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                revivalText.clearEntityModifiers();
                revivalText.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, new AlphaModifier(0.6f, 1, 0)));
                revivalSkillFrame.clearEntityModifiers();
                revivalSkillFrame.registerEntityModifier(new SequenceEntityModifier(showModifier, delayModifier, hideModifier, finishDelayModifier));
                applyParty.registerEntityModifier(hpDelayModifier);
            }
        });

    }

    private void useSkillMagicAttack(final BattlePartyFrame applyParty, final boolean isMyAction) {
        final AnimatedSprite magicEffectSprite = magicEffectSprites[applyParty.getParty().getPartyNumber() - 1];
        magicEffectSprite.setVisible(true);
        final float attackEffectOffsetY = isMyAction ? -50 : 50;
        magicEffectSprite.setPosition(applyParty.getX(), applyParty.getY() + attackEffectOffsetY);
        magicEffectSprite.animate(90, false, new IAnimationListener() {

            @Override
            public void onAnimationStarted(final AnimatedSprite pAnimatedSprite, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFrameChanged(final AnimatedSprite pAnimatedSprite, final int pOldFrameIndex, final int pNewFrameIndex) {

            }

            @Override
            public void onAnimationLoopFinished(final AnimatedSprite pAnimatedSprite, final int pRemainingLoopCount, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFinished(final AnimatedSprite pAnimatedSprite) {
                magicEffectSprite.setVisible(false);
            }

        });
    }

    private void useSkillGoodMagic(final BattlePartyFrame applyParty, final boolean isMyAction) {
        final AnimatedSprite effectSprite = goodEffectSprites[applyParty.getParty().getPartyNumber() - 1];
        effectSprite.setVisible(true);
        final float attackEffectOffsetY = isMyAction ? 50 : -55;
        effectSprite.setPosition(applyParty.getX(), applyParty.getY() + attackEffectOffsetY);
        effectSprite.animate(90, false, new IAnimationListener() {

            @Override
            public void onAnimationStarted(final AnimatedSprite pAnimatedSprite, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFrameChanged(final AnimatedSprite pAnimatedSprite, final int pOldFrameIndex, final int pNewFrameIndex) {

            }

            @Override
            public void onAnimationLoopFinished(final AnimatedSprite pAnimatedSprite, final int pRemainingLoopCount, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFinished(final AnimatedSprite pAnimatedSprite) {
                effectSprite.setVisible(false);
            }

        });
    }

    private void useSkillCure(final BattlePartyFrame applyParty, final boolean isMyAction) {
        final AnimatedSprite cureEffectSprite = cureEffectSprites[applyParty.getParty().getPartyNumber() - 1];
        cureEffectSprite.setVisible(true);
        final float attackEffectOffsetY = isMyAction ? 50 : -55;
        cureEffectSprite.setPosition(applyParty.getX(), applyParty.getY() + attackEffectOffsetY);
        cureEffectSprite.animate(90, false, new IAnimationListener() {

            @Override
            public void onAnimationStarted(final AnimatedSprite pAnimatedSprite, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFrameChanged(final AnimatedSprite pAnimatedSprite, final int pOldFrameIndex, final int pNewFrameIndex) {

            }

            @Override
            public void onAnimationLoopFinished(final AnimatedSprite pAnimatedSprite, final int pRemainingLoopCount, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFinished(final AnimatedSprite pAnimatedSprite) {
                cureEffectSprite.setVisible(false);
            }

        });
    }

    private void useSkillConfuse(final BattlePartyFrame applyParty, final boolean isMyAction) {
        final AnimatedSprite confuseEffectSprite = confuseEffectSprites[applyParty.getParty().getPartyNumber() - 1];
        confuseEffectSprite.setVisible(true);
        final float attackEffectOffsetY = isMyAction ? -55 : 45;
        confuseEffectSprite.setPosition(applyParty.getX(), applyParty.getY() + attackEffectOffsetY);
        confuseEffectSprite.animate(100, false, new IAnimationListener() {

            @Override
            public void onAnimationStarted(final AnimatedSprite pAnimatedSprite, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFrameChanged(final AnimatedSprite pAnimatedSprite, final int pOldFrameIndex, final int pNewFrameIndex) {

            }

            @Override
            public void onAnimationLoopFinished(final AnimatedSprite pAnimatedSprite, final int pRemainingLoopCount, final int pInitialLoopCount) {

            }

            @Override
            public void onAnimationFinished(final AnimatedSprite pAnimatedSprite) {
                confuseEffectSprite.setVisible(false);
            }

        });
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
                    if (party == null) {
                        continue;
                    }
                    if (party.getHp() > 0) {
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
                    if (party == null) {
                        continue;
                    }
                    if (party.getHp() > 0) {
                        applyParties.add(party);
                    }
                }
                break;
            case OpponentAll:
                for (final BattlePartyFrame party : opponentParties) {
                    if (party == null) {
                        continue;
                    }
                    if (party.getHp() > 0) {
                        applyParties.add(party);
                    }
                }
                break;
            default:
                applyParties.add(selfParty);
        }
        return applyParties;
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
            final OnFinishedCallback onFinishedCallback, final boolean isMyAction, final List<RevivalRecord> afterAttackRevivalRecords) {
        // final float initScale = actionPartyFrame.getScaleX();
        // final float initX = actionPartyFrame.getX();
        // final float initY = actionPartyFrame.getY();
        // final float attackDuration = 0.05f;
        // final float backDuration = 0.5f;
        // actionPartyFrame.setZIndex(10);
        // this.sortChildren();
        //
        // final float adjustY = actionPartyFrame.isBottom() ? -140 : 137;
        //
        // //
        //
        // final IEntityModifier attackScaleModifier = new ScaleModifier(attackDuration, initScale, 1, new ModifierFinishedListener(onHitCallback));
        // final IEntityModifier attackMoveModifier = new MoveModifier(attackDuration, initX, initY, defencePartyFrame.getX(), defencePartyFrame.getY() +
        // adjustY);
        // final IEntityModifier attackModifier = new ParallelEntityModifier(attackScaleModifier, attackMoveModifier);

        final OnFinishedCallback attackFinishCallback = createPreReviveCallback(actionPartyFrame, isMyAction, onFinishedCallback, afterAttackRevivalRecords);

        actionPartyFrame.attack(onHitCallback, attackFinishCallback);

        // final IEntityModifierListener backFinishListener = new ModifierFinishedListener(new OnFinishedCallback() {
        // @Override
        // public void onFinished(final IEntity pItem) {
        // pItem.setZIndex(IEntity.ZINDEX_DEFAULT);
        // BattleScene.this.sortChildren();
        // attackFinishCallback.onFinished(pItem);
        // }
        //
        // });
        //
        // final IEntityModifier backScaleModifier = new ScaleModifier(backDuration, 1, initScale, backFinishListener);
        // final IEntityModifier backMoveModifier = new MoveModifier(backDuration, defencePartyFrame.getX(), defencePartyFrame.getY() + adjustY, initX, initY);
        //
        // final IEntityModifier backModifier = new ParallelEntityModifier(backScaleModifier, backMoveModifier);
        //
        // final IEntityModifier actionModifier = new SequenceEntityModifier(new DelayModifier(0.8f), attackModifier, new DelayModifier(0.1f), backModifier);
        // activity.runOnUpdateThread(new Runnable() {
        // @Override
        // public void run() {
        // actionPartyFrame.clearEntityModifiers();
        // actionPartyFrame.registerEntityModifier(actionModifier);
        // }
        // });

    }

    private void revertParties(final BattlePartyFrame[] actionPartyFrames, final int actionPartyIndex, final OnFinishedCallback onFinishedCallback) {
        final float duration = 0.2f;
        for (int i = 0; i < 3; i++) {
            final BattlePartyFrame partyFrame = actionPartyFrames[i];
            if (partyFrame == null) {
                continue;
            }
            final IEntityModifier delayModifier = new DelayModifier(1f);
            final IEntityModifier scaleModifier = new ScaleModifier(duration, partyFrame.getScaleX(), 1);
            final IEntityModifier moveModifier = new MoveModifier(duration, partyFrame.getX(), partyFrame.getY(), partyFrame.getInitX(), partyFrame.getInitY());
            final IEntityModifier modifier = new SequenceEntityModifier(delayModifier, new ParallelEntityModifier(scaleModifier, moveModifier));
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

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}
