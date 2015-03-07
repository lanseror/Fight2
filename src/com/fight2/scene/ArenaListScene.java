package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Arena;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ArenaUtils;
import com.fight2.util.F2SoundManager;
import com.fight2.util.ResourceManager;

public class ArenaListScene extends BaseScene {
    private final PartyInfo partyInfo = GameUserSession.getInstance().getPartyInfo();
    private final float topbarY = cameraHeight - TextureEnum.ARENA_TOPBAR.getHeight();
    private final float listFrameY = topbarY - TextureEnum.ARENA_LIST_FRAME.getHeight() + 10;
    private final float listFrameHeight = TextureEnum.ARENA_LIST_FRAME.getHeight();
    private final Font topBarFont;
    private final Text hpText;
    private final Text atkText;
    private final Text ticketText;
    private final Sprite listFrame;
    private final Sprite listSelectedBar;
    private final float arenaStartY = listFrameHeight - 90;
    private final List<IEntity> arenaEntities = new ArrayList<IEntity>();

    public ArenaListScene(final GameActivity activity) throws IOException {
        super(activity);
        this.topBarFont = ResourceManager.getInstance().newFont(FontEnum.Main);
        hpText = new Text(280, 48, topBarFont, "0123456789", vbom);
        atkText = new Text(480, 48, topBarFont, "0123456789", vbom);
        ticketText = new Text(670, 48, topBarFont, "0123456789", vbom);
        hpText.setText(String.valueOf(partyInfo.getHp()));
        atkText.setText(String.valueOf(partyInfo.getAtk()));
        ticketText.setText("0");
        listFrame = createALBImageSprite(TextureEnum.ARENA_LIST_FRAME, this.simulatedLeftX, listFrameY);
        this.attachChild(listFrame);
        listSelectedBar = createALBImageSprite(TextureEnum.ARENA_LIST_SELECTED, 15, arenaStartY);
        listSelectedBar.setVisible(false);
        listFrame.attachChild(listSelectedBar);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.BATTLE_ARENA_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topbarSprite = createALBImageSprite(TextureEnum.ARENA_TOPBAR, this.simulatedLeftX, topbarY);
        this.attachChild(topbarSprite);
        topbarSprite.attachChild(hpText);
        topbarSprite.attachChild(atkText);
        topbarSprite.attachChild(ticketText);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 35);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                F2SoundManager.getInstance().play(SoundEnum.BUTTON_CLICK);
                ResourceManager.getInstance().sceneBack();
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite enterButton = createALBF2ButtonSprite(TextureEnum.ARENA_LIST_ENTER, TextureEnum.ARENA_LIST_ENTER_PRESSED, 580, -2);
        enterButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                F2SoundManager.getInstance().play(SoundEnum.BUTTON_CLICK2);
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Arena);
            }
        });
        listFrame.attachChild(enterButton);
        this.registerTouchArea(enterButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);

    }

    @Override
    public void updateScene() {
        for (final IEntity arenaEntity : arenaEntities) {
            listFrame.detachChild(arenaEntity);
        }
        arenaEntities.clear();

        final Font font = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final List<Arena> arenas = ArenaUtils.getArenas(activity);

        for (int i = 0; i < arenas.size(); i++) {
            final Arena arena = arenas.get(i);
            final float arenaY = arenaStartY - i * 65;
            final Text nameText = new Text(100, arenaY, font, arena.getName(), vbom);
            nameText.setX(30 + nameText.getWidth() * 0.5f);
            final Text timeText = new Text(360, arenaY, font, arena.getRemainTime(), vbom);
            final Text onlineText = new Text(500, arenaY, font, String.valueOf(arena.getOnlineNumber()), vbom);
            final Sprite line = createACImageSprite(TextureEnum.ARENA_LIST_LINE, 280, arenaY - 32);
            listFrame.attachChild(nameText);
            listFrame.attachChild(timeText);
            listFrame.attachChild(onlineText);
            listFrame.attachChild(line);
            arenaEntities.add(nameText);
            arenaEntities.add(timeText);
            arenaEntities.add(onlineText);
            arenaEntities.add(line);

            final int arenaId = arena.getId();
            if (ArenaUtils.getSelectedArenaId() == arenaId) {
                listSelectedBar.setY(arenaY);
                listSelectedBar.setVisible(true);
            }

            final IEntity touchArea = createListTouchArea(arena, arenaY);
            listFrame.attachChild(touchArea);
            this.registerTouchArea(touchArea);
        }
        if (!listSelectedBar.isVisible() && !arenas.isEmpty()) {
            listSelectedBar.setY(arenaStartY);
            listSelectedBar.setVisible(true);
            ArenaUtils.setSelectedArena(arenas.get(0));
        }
        activity.getGameHub().needSmallChatRoom(true);
    }

    private IEntity createListTouchArea(final Arena arena, final float arenaY) {
        final IEntity touchArea = new Rectangle(280, arenaY, 530, 55, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    listSelectedBar.setY(arenaY);
                    ArenaUtils.setSelectedArena(arena);
                }
                return true;
            }
        };
        touchArea.setAlpha(0);
        return touchArea;
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }
}
