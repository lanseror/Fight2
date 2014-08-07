package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
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
import org.andengine.util.modifier.IModifier;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.BattlePartyFrame;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
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

    public BattleScene(final GameActivity activity, final int attackPlayerId) throws IOException {
        super(activity);
        opponentParties = CardUtils.getPartyByUserId(activity, attackPlayerId).getParties();
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
                iAttack(2, 0);
                // hpBar.setCurrentPoint(5000);
            }
        });
        this.attachChild(tButton);
        this.registerTouchArea(tButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        updateScene();
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

    public void iAttack(final int actionPartyFrameIndex, final int defencePartyFrameIndex) {
        final float frameHeight = myPartyFrames[actionPartyFrameIndex].getHeight();
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

        final BattlePartyFrame actionPartyFrame = myPartyFrames[actionPartyFrameIndex];
        final BattlePartyFrame defencePartyFrame = opponentPartyFrames[defencePartyFrameIndex];

        for (int i = 0; i < 3; i++) {
            final BattlePartyFrame partyFrame = myPartyFrames[i];
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
            final float duration = 0.2f;
            final float attackDuration = 0.05f;
            final float backDuration = 0.5f;

            final IEntityModifier scaleModifier = new ScaleModifier(duration, 1, chaneScale);
            final IEntityModifier moveModifier = new MoveModifier(duration, partyFrame.getInitX(), partyFrame.getInitY(), FRAME_START_X + changeX, changeY);
            final IEntityModifier modifier = new ParallelEntityModifier(scaleModifier, moveModifier);
            partyFrame.clearEntityModifiers();
            if (i == actionPartyFrameIndex) {
                partyFrame.setZIndex(10);
                this.sortChildren();
                final IEntityModifier attackScaleModifier = new ScaleModifier(attackDuration, chaneScale, 1);
                final IEntityModifier attackMoveModifier = new MoveModifier(attackDuration, FRAME_START_X + changeX, changeY, defencePartyFrame.getX(),
                        defencePartyFrame.getY() - 150);
                final IEntityModifier attackModifier = new ParallelEntityModifier(attackScaleModifier, attackMoveModifier);

                final IEntityModifier backScaleModifier = new ScaleModifier(backDuration, 1, chaneScale);
                final IEntityModifier backMoveModifier = new MoveModifier(backDuration, defencePartyFrame.getX(), defencePartyFrame.getY() - 150, FRAME_START_X
                        + changeX, changeY);
                final IEntityModifier backModifier = new ParallelEntityModifier(backScaleModifier, backMoveModifier);
                final IEntityModifierListener finishListener = new IEntityModifierListener() {
                    @Override
                    public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {

                    }

                    @Override
                    public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                        revertParties();
                        pItem.setZIndex(IEntity.ZINDEX_DEFAULT);
                        BattleScene.this.sortChildren();
                    }

                };
                final IEntityModifier actionModifier = new SequenceEntityModifier(finishListener, modifier, new DelayModifier(0.3f), attackModifier,
                        new DelayModifier(0.1f), backModifier);
                partyFrame.registerEntityModifier(actionModifier);
            } else {
                partyFrame.registerEntityModifier(modifier);
            }

        }

    }

    private void revertParties() {
        final float duration = 0.2f;
        for (int i = 0; i < 3; i++) {
            final BattlePartyFrame partyFrame = myPartyFrames[i];
            final IEntityModifier scaleModifier = new ScaleModifier(duration, partyFrame.getScaleX(), 1);
            final IEntityModifier moveModifier = new MoveModifier(duration, partyFrame.getX(), partyFrame.getY(), partyFrame.getInitX(), partyFrame.getInitY());
            final IEntityModifier modifier = new ParallelEntityModifier(scaleModifier, moveModifier);
            partyFrame.registerEntityModifier(modifier);
        }
    }

    private BattlePartyFrame createPartyContainer(final Party party, final float x, final float y, final boolean isBottom) {
        return new BattlePartyFrame(x, y, party, vbom, isBottom);
    }

    private BattlePartyFrame createOpponentPartyContainer(final Party party, final float x, final float y, final boolean isBottom) {
        return new BattlePartyFrame(x, y, party, vbom, isBottom);
    }

}
